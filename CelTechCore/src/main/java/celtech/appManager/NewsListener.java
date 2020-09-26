package celtech.appManager;

import celtech.appManager.NewsBot.NewsArticle;
import java.util.List;

/**
 *
 * @author Ian
 */
public interface NewsListener
{
    public void hereIsTheNews(List<NewsArticle> newsArticles);
}
