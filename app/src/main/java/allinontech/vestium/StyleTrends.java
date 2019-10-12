package allinontech.vestium;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import allinontech.vestium.backend.Link;

/**
 *  StyleTrends class execute the linkRetrieverAsyncTask depend on the user's gender and displays fashion articles from "Hello Fashion" ans "The Idle Man" pages.
 */
public class StyleTrends extends AppCompatActivity {
    private RecyclerView styleTrendsRecyclerView;
    private StyleTrendsLinkRetriever linkRetrieverAsyncTask;
    private WebView mWebView;
    private final String mGENDER = "male";
    private final String fGENDER = "female";

    //get the gender from homepage
    String gender;

    /**
     * When page is created, task is executed depend on the gender.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_trends);

        if( HomeScreen.gender.equals("male"))
            gender = "male";
        else
            gender = "female";

        linkRetrieverAsyncTask = new StyleTrendsLinkRetriever();

        //if the genter is female, use this link
        if( gender.equals(fGENDER)) {
            linkRetrieverAsyncTask.execute("https://www.hellofashionblog.com/category/fashion");
        }

        //if the gender is male, use this link
        if( gender.equals(mGENDER)) {
            linkRetrieverAsyncTask.execute("https://theidleman.com/manual/");
        }



        mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);

        //A View that displays web pages. Access the URL and use full content.
        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
    }

    // *********************************************************************************************

    /**
     *This class takes single style trends item, and put them in a recycler view in a neat way.
     */

    public class styleTrendsAdapter extends RecyclerView.Adapter<TrendViewHolder> implements View.OnClickListener{
        Context c;
        ArrayList<Link> styleTrendsContainer;

        public styleTrendsAdapter( Context c, ArrayList<Link> styleTrendsContainer) {
            this.c = c;
            this.styleTrendsContainer = styleTrendsContainer;
        }

        @Override
        public TrendViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
            View v = LayoutInflater.from( c).inflate(R.layout.single_styletrends_item, parent, false);
            v.setOnClickListener(this);
            return new TrendViewHolder(c, v);
        }

        @Override
        public void onBindViewHolder( TrendViewHolder holder, int position) {

            holder.setName(styleTrendsContainer.get(position).getTitle());
            holder.setImage(styleTrendsContainer.get(position).getImageLink());
        }

        @Override
        public int getItemCount() {
            return styleTrendsContainer.size();
        }

        @Override
        public void onClick(View v) {

            int position = styleTrendsRecyclerView.getChildLayoutPosition(v);
            mWebView.loadUrl(styleTrendsContainer.get(position).getFashionLink());
            setContentView( mWebView);
            // go to link styleTrendsContainer.get(position).getFashionLink()
        }
    }

    // single_styletrends_item
    public class TrendViewHolder extends RecyclerView.ViewHolder {
        View styleTrendsView;
        Context c;


        public TrendViewHolder(Context c, View styelTrendview) {
            super(styelTrendview);
            this.styleTrendsView = styelTrendview;
            this.c = c;
        }

        public void setName( String title) {
            TextView styleTrendName = (TextView) styleTrendsView.findViewById(R.id.styleTrendsName);
            styleTrendName.setText( title);
        }

        public void setImage( String url) {
            ImageView styleTrendsImage = (ImageView) styleTrendsView.findViewById(R.id.styleTrendsImage);
            Glide.with(c)
                    .load(url)
                    .into(styleTrendsImage);
        }

    }

    // *********************************************************************************************

    public class StyleTrendsLinkRetriever extends AsyncTask<String, Void, ArrayList<Link>> {



        public StyleTrendsLinkRetriever() {

        }

        @Override
        protected ArrayList<Link> doInBackground(String... strings) {
            String url = strings[0];
            String html = "";

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                InputStream internalInputStream = response.getEntity().getContent();


                StringBuilder str = new StringBuilder();
                Log.d("inGetcontent", "getContent: " + internalInputStream);
                try {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(internalInputStream));

                    String line = null;

                    while ((line = reader.readLine()) != null) {

                        str.append(line + "\n");
                    }
                    internalInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                html = str.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }


            String pageContent = html;

            // Esra and Sara's code

            //for women
            if (gender.equals(fGENDER)) {
                pageContent = pageContent.substring(pageContent.indexOf(
                        "<li><a href=\"https://www.hellofashionblog.com/category/beauty\" class=\"dropdown-link\" data-id=\"15781\">Beauty</a></li>"));
                ArrayList links = new ArrayList<Link>();

                while (pageContent.indexOf("data-image=") != -1)
                {


                    //find image link
                    int imageIndexBeginning = pageContent.indexOf("data-image=") + 12;
                    int imageIndexEnd = pageContent.indexOf("></div>", pageContent.indexOf("data-image="));
                    String imageUrl = pageContent.substring(imageIndexBeginning, imageIndexEnd);

                    //find article link
                    int linkIndexBeginning = pageContent.indexOf("href=", pageContent.indexOf("data-image=")) + 6;
                    int linkIndexEnd = pageContent.indexOf("data-title") - 2;
                    String fashionUrl = pageContent.substring(linkIndexBeginning, linkIndexEnd);

                    //find title
                    int titleIndexBeginning = pageContent.indexOf("data-title=", pageContent.indexOf("data-image=")) + 12;
                    int titleIndexEnd = pageContent.indexOf("style", titleIndexBeginning) - 2;
                    String unprocessedTitle = pageContent.substring(titleIndexBeginning, titleIndexEnd);

                    String title;
                    if (unprocessedTitle.contains("&#8217;")) {
                        title = unprocessedTitle.replaceAll("&#8217;", "'");
                    } else {
                        title = unprocessedTitle;
                    }

                    Link link = new Link(fashionUrl, imageUrl, title);

                    if (isUnique(links, link)) {
                        links.add(link);
                    }

                    pageContent = pageContent.substring(titleIndexEnd + 3, pageContent.length());

                }


                return links;
            }


            else
            {
                ArrayList links = new ArrayList<Link>();

                while( pageContent.contains("<div class=\"entry-image\">") )
                {

                    //find image link
                    int imageIndexBeginning = pageContent.indexOf("src=\"", pageContent.indexOf("<div class=\"entry-image\">")) + 5;
                    int imageIndexEnd = pageContent.indexOf(".jpg", imageIndexBeginning) + 4;
                    String imageUrl = pageContent.substring( imageIndexBeginning , imageIndexEnd);

                    //find article link
                    int linkIndexBeginning = pageContent.indexOf("href=" , pageContent.indexOf("<div class=\"entry-image\">") ) + 6;
                    int linkIndexEnd= pageContent.indexOf("\" title=\"" , linkIndexBeginning);
                    String fashionUrl =  pageContent.substring( linkIndexBeginning ,linkIndexEnd );

                    //find title
                    int titleIndexBeginning = pageContent.indexOf("title=\"" , pageContent.indexOf("<div class=\"entry-image\">")) +7;
                    int titleIndexEnd= pageContent.indexOf("\">" , titleIndexBeginning);
                    String title =  pageContent.substring( titleIndexBeginning ,titleIndexEnd );

                    String filteredTitle;
                    filteredTitle = title.replaceAll( "&#8217;" , "'");

                    Link link = new Link( fashionUrl , imageUrl, filteredTitle );
                    links.add(link);
                    pageContent = pageContent.substring(imageIndexEnd + 50, pageContent.length() );

                }
                return links;
            }
        }

        public boolean isUnique( ArrayList<Link> links, Link link) {
            int count = 0;
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).getTitle().equals(link.getTitle())) {
                    count++;
                }

            }
            if (count == 0)
            {
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(ArrayList links) {
            styleTrendsRecyclerView = (RecyclerView) findViewById(R.id.styleTrendsRecyclerView);
            styleTrendsRecyclerView.setLayoutManager(new LinearLayoutManager( StyleTrends.this));
            styleTrendsRecyclerView.setAdapter( new styleTrendsAdapter( StyleTrends.this, links));


        }


    }





}
