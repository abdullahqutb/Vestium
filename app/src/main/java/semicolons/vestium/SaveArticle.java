package semicolons.vestium;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SaveArticle extends AppCompatActivity {


    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_article);

        Bundle bundle = getIntent().getExtras();
        //photoURI = Uri.parse(bundle.get("imagePath"));
    }
}
