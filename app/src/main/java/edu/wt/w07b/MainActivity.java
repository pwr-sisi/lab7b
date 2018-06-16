package edu.wt.w07b;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    StackOverflowAPI stackOverflowAPI;
    EditText editQueryString;
    ListWrapper<ItemQuestion> questions;
    ListView questionsListView;
    Context context = this;
    TextView questionsFound;
    ItemQuestionAdapter itemQuestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editQueryString = findViewById(R.id.editQueryString);
        questions = new ListWrapper<ItemQuestion>();
        questions.items = new ArrayList<ItemQuestion>();
        questionsListView = findViewById(R.id.questionsListView);
        questionsListView.setAdapter(new ItemQuestionAdapter());
        questionsFound = findViewById(R.id.questionsFound);

        createStackOverflowAPI();
    }

    // Utworzenie obiektu wykonującego zapytania do API
    private void createStackOverflowAPI() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StackOverflowAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        stackOverflowAPI = retrofit.create(StackOverflowAPI.class);
    }


    // Uruchomienie zapytania do API po kliknięciu przycisku.
    // Tytuł szukanego zapytania pochodzi z pola tekstowego editQueryString
    public void setCatalogContent(View button) {
        String title = editQueryString.getText().toString();
        stackOverflowAPI.getQuestions(title).enqueue(questionsCallback);
    }


    // Funkcje zwrotne wywoływane po zakończeniu zapytania API
    // Zawsze trzeba zdefiniować zachowanie Retrofita po udanym wywołaniu (onResponse)
    // i po błędzie (onFailure)
    Callback<ListWrapper<ItemQuestion>> questionsCallback = new Callback<ListWrapper<ItemQuestion>>() {
        @Override
        public void onResponse(Call<ListWrapper<ItemQuestion>> call, Response<ListWrapper<ItemQuestion>> response) {
            if (response.isSuccessful()) {
                // GSON automatycznie tłumaczy pobrany kod JSON na obiekty Javy
                questions = response.body();
                ((ItemQuestionAdapter)questionsListView.getAdapter()).notifyDataSetChanged();
                questionsListView.setSelectionAfterHeaderView();
                questionsFound.setText("We have found " + questions.items.size() + " questions");
            } else {
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<ListWrapper<ItemQuestion>> call, Throwable t) {
            t.printStackTrace();
        }
    };


    // Adapter pozwalający wyświetlać listę obiektów w liście
    private class ItemQuestionAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;

        public ItemQuestionAdapter() {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return questions.items.size();
        }

        @Override
        public Object getItem(int position) {
            return questions.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            if(rowView == null) {
                rowView = layoutInflater.inflate(R.layout.item_question,null);
            }
            TextView questionText = rowView.findViewById(R.id.question);
            TextView questionLink;
            questionLink = rowView.findViewById(R.id.link);
            TextView questionId = rowView.findViewById(R.id.question_id);
            ItemQuestion iq = questions.items.get(position);
            questionText.setText(iq.getTitle());
            questionLink.setText(iq.getLink());
            questionId.setText(Long.toString(iq.getId()));

            return rowView;
        }
    }
}
