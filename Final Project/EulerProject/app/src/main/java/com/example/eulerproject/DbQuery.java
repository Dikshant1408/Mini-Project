package com.example.eulerproject;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.example.eulerproject.Models.CategoryModel;
import com.example.eulerproject.Models.ProfileModel;
import com.example.eulerproject.Models.QuestionModel;
import com.example.eulerproject.Models.RankModel;
import com.example.eulerproject.Models.TestModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbQuery {

    public static List<CategoryModel> g_catList = new ArrayList<>();
    public static int g_selected_cat_index = 0;
    public static List<TestModel> g_testList = new ArrayList<>();
    public static int g_selected_test_index = 0;
    public static List<QuestionModel> g_questList = new ArrayList<>();
    public static FirebaseFirestore  g_firstore;
    public static ProfileModel myProfile = new ProfileModel("NA",null);
    public static RankModel myPerformance = new RankModel(0,-1);
    public static final int NOT_VISITED = 0;
    public static final int UNANSWERED = 1;
    public static final int ANSWERED = 2;
    public static final int REVIEW = 3;

    public static void createUserData(String email, String name,final MyCompleteListener completeListener){

        Map<String, Object> userData = new ArrayMap<>();
        userData.put("EMAIL_ID", email);
        userData.put("NAME",name);
        userData.put("TOTAL_SCORE",0);

        DocumentReference userDoc = g_firstore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firstore.batch();
        batch.set(userDoc,userData);
        DocumentReference countDoc = g_firstore.collection("USERS").document("TOTAL_USERS");
        batch.update(countDoc,"COUNT", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aViod) {
                        completeListener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public static void getUserData(final MyCompleteListener completeListener){
        g_firstore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        myProfile.setName(documentSnapshot.getString("NAME"));
                        myProfile.setEmail(documentSnapshot.getString("EMAIL_ID"));

                        myPerformance.setScore(documentSnapshot.getLong("TOTAL_SCORE").intValue());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static void loadMyScore(MyCompleteListener completeListener){

        g_firstore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_SCORES")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        for (int i=0; i< g_testList.size(); i++){

                            int top = 0;
                            if (documentSnapshot.get(g_testList.get(i).getTestID()) != null){
                                top = documentSnapshot.getLong(g_testList.get(i).getTestID()).intValue();
                            }

                            g_testList.get(i).setTopScore(top);
                        }
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public static void saveResult(int score, MyCompleteListener completeListener){

        WriteBatch batch = g_firstore.batch();
        DocumentReference userDoc = g_firstore.collection("USERS").document(FirebaseAuth.getInstance().getUid());
        batch.update(userDoc, "TOTAL_SCORE",score);

        if (score > g_testList.get(g_selected_test_index).getTopScore()){
            DocumentReference scoreDoc = userDoc.collection("USER_DATA").document("MY_SCORES");

            Map<String , Object> testData = new ArrayMap<>();
            testData.put(g_testList.get(g_selected_test_index).getTestID(), score);
            batch.set(scoreDoc, testData, SetOptions.merge());
        }

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        if (score > g_testList.get(g_selected_test_index).getTopScore()){
                            g_testList.get(g_selected_test_index).setTopScore(score);

                            myPerformance.setScore(score);
                            completeListener.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public static void loadCategories(final MyCompleteListener completeListener){
        g_catList.clear();
        g_firstore.collection("QUIZ").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            docList.put(doc.getId(), doc);
                        }

                        QueryDocumentSnapshot catListDoc = docList.get("Categories");

                        long catCount = catListDoc.getLong("COUNT");

                        for(int i = 1; i <=catCount ; i++){
                            String catID = catListDoc.getString("CAT" + String.valueOf(i) + "_ID");
                            QueryDocumentSnapshot catDoc = docList.get(catID);
                            int noOfTest = catDoc.getLong("NO_OF_TESTS").intValue();
                            String catNAme = catDoc.getString("NAME");

                            g_catList.add(new CategoryModel(catID, catNAme, noOfTest));
                        }

                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public static void loadquestions(MyCompleteListener myCompleteListener){

        g_questList.clear();
        g_firstore.collection("Questions")
                .whereEqualTo("CATEGORY", g_catList.get(g_selected_cat_index).getDocID())
                .whereEqualTo("TEST",g_testList.get(g_selected_test_index).getTestID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {

                            g_questList.add(new QuestionModel(
                                    doc.getString("QUESTION"),
                                    doc.getString("A"),
                                    doc.getString("B"),
                                    doc.getString("C"),
                                    doc.getString("D"),
                                    doc.getLong("ANSWER").intValue(),
                                    -1,
                                    NOT_VISITED
                            ));
                        }
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void loadTestData(final MyCompleteListener completeListener){
        g_testList.clear();

        g_firstore.collection("QUIZ").document(g_catList.get(g_selected_cat_index).getDocID())
                .collection("TEST_LIST").document("TEST_INFO")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        int noOfTests = g_catList.get(g_selected_cat_index).getNoOfTest();

                        for (int i = 1; i <= noOfTests; i++) {
                            Long testTimeLong = documentSnapshot.getLong("TEST" + i + "_TIME");
                            int testTime = testTimeLong != null ? testTimeLong.intValue() : 0;
                            g_testList.add(new TestModel(
                                    documentSnapshot.getString("TEST" + i + "_ID"),
                                    0,
                                    testTime
                            ));
                        }
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public static void loadData(final MyCompleteListener completeListener){
        loadCategories(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                getUserData(completeListener);
            }

            @Override
            public void onFailure() {
                completeListener.onFailure();
            }
        });
    }
}
