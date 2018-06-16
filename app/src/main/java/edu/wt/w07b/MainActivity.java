package edu.wt.w07b;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    StackOverflowAPI stackOverflowAPI;
    EditText editQueryString;
    QuestionsList<Question> questions;
    ListView questionsListView;
    Context context = this;
    TextView questionsFound;
    ItemQuestionAdapter itemQuestionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editQueryString = findViewById(R.id.editQueryString);
        questions = new QuestionsList<Question>();
        questions.items = new ArrayList<Question>();
        questionsListView = findViewById(R.id.questionsListView);
        questionsListView.setAdapter(new ItemQuestionAdapter());
        questionsFound = findViewById(R.id.questionsFound);

        createStackOverflowAPI();
    }

    // Utworzenie obiektu wykonującego zapytania do API
    private void createStackOverflowAPI() {
        // Filtr GSON będzie automatycznie tłumaczył pobrany plik JSON na obiekty Javy
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        // Fabryka buduje obiekt retrofit służący do pobierania danych
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StackOverflowAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Fabryka buduje obiekt dla naszego API
        stackOverflowAPI = retrofit.create(StackOverflowAPI.class);
    }


    // Uruchomienie zapytania do API po kliknięciu przycisku.
    // Tytuł szukanego zapytania pochodzi z pola tekstowego editQueryString
    // Klasa QuestionsCallback obsłuży metodę zwrotną
    public void setCatalogContent(View button) {
        String title = editQueryString.getText().toString();
        stackOverflowAPI.getQuestions(title).enqueue(questionsCallback);
    }


    // Funkcje zwrotne wywoływane po zakończeniu zapytania API
    // Zawsze trzeba zdefiniować zachowanie Retrofita po udanym wywołaniu (onResponse)
    // i po błędzie (onFailure)
    Callback<QuestionsList<Question>> questionsCallback = new Callback<QuestionsList<Question>>() {
        @Override
        public void onResponse(Call<QuestionsList<Question>> call, Response<QuestionsList<Question>> response) {
            if (response.isSuccessful()) {
                // Pobranie danych z odpowiedzi serwera
                questions = response.body();

                // Odświeżenie widoku listy i informacji o pobranych danych
                ((ItemQuestionAdapter)questionsListView.getAdapter()).notifyDataSetChanged();
                questionsListView.setSelectionAfterHeaderView();
                questionsFound.setText("We have found " + questions.items.size() + " questions");
            } else {
                Log.d("QuestionsCallback", "Code: " + response.code() + " Message: " + response.message());
            }
        }

        @Override
        public void onFailure(Call<QuestionsList<Question>> call, Throwable t) {
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
            Question currentQuestion = questions.items.get(position);
            questionText.setText(currentQuestion.getTitle());
            questionLink.setText(currentQuestion.getLink());
            questionId.setText(Long.toString(currentQuestion.getId()));

            return rowView;
        }
    }
}
