package crawler;

import java.io.IOException;
import java.io.PrintWriter;

public class Test {
    private static final String RANK_MARKER = "<em class=\"\">";
    private static final String TITLE_MARKER = "<span class=\"title\">";
    private static final String DIRECTOR_MARKER = "导演: ";
    private static final String ACTOR_MARKER = "&nbsp;&nbsp;&nbsp;主演: ";
    private static final String RATING_MARKER = "<span class=\"rating_num\" property=\"v:average\">";

    public static void main(String[] args) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=25"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=50"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=75"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=100"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=125"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=150"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=175"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=200"));
        htmlContent.append(HtmlFetcher.fetchHtmlContent("https://movie.douban.com/top250?start=225"));
        StringBuilder movieInfo = new StringBuilder();
        movieInfo.append("豆瓣电影 Top 250\n\n");
        int searchFrom = 0;
        String html = htmlContent.toString();
        while (true) {
            int rankStart = html.indexOf(RANK_MARKER, searchFrom);
            if (rankStart == -1) {
                break;
            }
            int nextRank = html.indexOf(RANK_MARKER, rankStart + RANK_MARKER.length());
            String block = html.substring(rankStart, nextRank == -1 ? html.length() : nextRank);
            String rank = between(block, RANK_MARKER, "</em>");
            String title = between(block, TITLE_MARKER, "</span>");
            String director = between(block, DIRECTOR_MARKER, ACTOR_MARKER);
            String actor = between(block, ACTOR_MARKER, "<br>");
            String year = firstYearBefore(block, "&nbsp;/&nbsp;");
            String rating = between(block, RATING_MARKER, "</span>");
            if (!rank.isEmpty() && !title.isEmpty() && !rating.isEmpty()) {
                movieInfo.append("排名: ").append(rank).append("\n");
                movieInfo.append("电影名: ").append(title).append("\n");
                movieInfo.append("导演: ").append(director).append("\n");
                movieInfo.append("主演: ").append(actor).append("\n");
                movieInfo.append("年份: ").append(year).append("\n");
                movieInfo.append("评分: ").append(rating).append("\n");
                movieInfo.append("\n");
            }
            searchFrom = nextRank == -1 ? html.length() : nextRank;
        }
        try (PrintWriter out = new PrintWriter("crawler\\movieInfo.txt")) {
            out.println(movieInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String between(String text, String startMarker, String endMarker) {
        int start = text.indexOf(startMarker);
        if (start == -1) {
            return "";
        }
        start += startMarker.length();
        int end = text.indexOf(endMarker, start);
        if (end == -1) {
            return "";
        }
        return text.substring(start, end).trim();
    }

    private static String firstYearBefore(String text, String endMarker) {
        int end = text.indexOf(endMarker);
        if (end == -1) {
            return "";
        }
        for (int index = Math.max(0, end - 16); index + 4 <= end; index++) {
            String candidate = text.substring(index, index + 4);
            if (candidate.chars().allMatch(Character::isDigit)) {
                return candidate;
            }
        }
        return "";
    }
}
