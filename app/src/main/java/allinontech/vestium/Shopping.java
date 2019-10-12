package allinontech.vestium;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
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


public class Shopping extends AppCompatActivity {

    private RecyclerView shoppingRecyclerView;
    private Shopping.ShoppingLinksRetriever linkRetrieverAsyncTask;
    private WebView mWebView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);


        Toolbar toolbar = (Toolbar) findViewById(R.id.shoppingToolbar);
        setSupportActionBar(toolbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("SHOPPING");
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        linkRetrieverAsyncTask = new Shopping.ShoppingLinksRetriever();
        String link;
        if( HomeScreen.gender.equals("male")){
            link = "https://www.trendyol.com/erkek?qs=navigation";
        }
        else{
            link = "https://www.trendyol.com/kadin?qs=navigation";
        }

        linkRetrieverAsyncTask.execute( link);
        mWebView = new WebView(this);
        mWebView.getSettings().setJavaScriptEnabled(true);

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


    // ******************************************************************************************
    public class shoppingAdapter extends RecyclerView.Adapter<Shopping.ShopViewHolder> implements View.OnClickListener{
        Context c;
        ArrayList<Link> shoppingItemContainer;

        public shoppingAdapter( Context c, ArrayList<Link> shoppingItemContainer) {
            this.c = c;
            this.shoppingItemContainer = shoppingItemContainer;
        }

        @Override
        public Shopping.ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from( c).inflate(R.layout.single_shopping_item, parent, false);
            v.setOnClickListener(this);
            return new Shopping.ShopViewHolder(v);
        }

        @Override
        public void onBindViewHolder(Shopping.ShopViewHolder holder, int position) {

            holder.setNameAndPrice(shoppingItemContainer.get(position).getTitle(), shoppingItemContainer.get(position).getPrice());
            holder.setImage(c, shoppingItemContainer.get(position).getImageLink());
        }

        @Override
        public int getItemCount() {
            return shoppingItemContainer.size();
        }

        @Override
        public void onClick(View v) {
            // go to internet
            int position = shoppingRecyclerView.getChildLayoutPosition(v);
            mWebView.loadUrl(shoppingItemContainer.get(position).getFashionLink());
            setContentView( mWebView);
        }
    }

    // single_styletrends_item
    public class ShopViewHolder extends RecyclerView.ViewHolder {
        View shoppingItemView;

        public ShopViewHolder(View shoppingItemView) {
            super(shoppingItemView);
            this.shoppingItemView = shoppingItemView;
        }

        public void setNameAndPrice( String title, String price) {
            TextView shoppingItemTitle = (TextView) shoppingItemView.findViewById(R.id.shoppingItemName);
            TextView shoppingItemPrice = (TextView) shoppingItemView.findViewById(R.id.shoppingItemPrice);
            shoppingItemTitle.setText( title);
            shoppingItemPrice.setText( price + " TL");

        }

        public void setImage( Context c, String url) {
            ImageView shoppingItemImage = (ImageView) shoppingItemView.findViewById(R.id.shoppingItemImage);
            Glide.with(c)
                    .load(url)
                    .into(shoppingItemImage);
        }
    }


    // ******************************************************************************************
    public class ShoppingLinksRetriever extends AsyncTask<String, Void, ArrayList<Link>> {



        public ShoppingLinksRetriever() {

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





            ArrayList links = new ArrayList<Link>();

            // Ahmed's code
            int index;
            int j;
            index = 0;
            for( int i = 0; i <= 20; i++) {
                Link link;
                link = new Link();
                index = html.indexOf( "product-card-wrapper", index);
                index = html.indexOf( "href=", index);
                for( j = index + 6; html.charAt( j) != '\"'; j++);
                // set link address
                link.setFashionLink("https://www.trendyol.com" + html.substring( index + 6, j));
                index = html.indexOf( "data-sa", index);
                for( j = index + 16; html.charAt( j) != '\"'; j++);
                // set price
                link.setPrice( html.substring( index + 16, j));
                index = html.indexOf( "org.jpg", index);
                for( j = index; html.charAt( j) != 'h'; j--);
                // set image link
                link.setImageLink( html.substring( j, index + 7));
                index = html.indexOf( "title=", index);
                for( j = index + 7; html.charAt( j) != '\"'; j++);
                link.setTitle( html.substring( index + 7, j));
                links.add( link);

            }

            Log.d("doInBakcground", "doInBackground size:" + links.size() + "    " + links);
            return links;
        }

        @Override
        protected void onPostExecute(ArrayList links) {
            shoppingRecyclerView = (RecyclerView) findViewById(R.id.shoppingRecyclerView);
            shoppingRecyclerView.setLayoutManager(new LinearLayoutManager( Shopping.this));
            shoppingRecyclerView.setAdapter( new Shopping.shoppingAdapter( Shopping.this, links));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

}
