package com.example.yoon.vtrproject.repo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.yoon.vtrproject.MyApplication;
import com.example.yoon.vtrproject.view.ReportListPresenter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataRepository extends AppCompatActivity {
    private Document document;
    private List<Node> documentItems = new ArrayList<>();


    private static class HOLDER {
        public static final DataRepository instance = new DataRepository();
    }

    public synchronized static DataRepository get() {
        return HOLDER.instance;
    }

    public void load(ReportListPresenter.LoadListener listener, String filename) {

        //String filename1 = filename;
        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/htmlData/"+"애국가.html";
        //String path = Environment.getExternalStorageDirectory() + "/htmlData/" + "test.html";

        //String path = Environment.getExternalStorageDirectory() + "/htmlData/" +  "Report of happy birthday.html";
        String path = Environment.getExternalStorageDirectory() + "/htmlData/" + filename + ".html";

        File file = new File(path);
        StringBuffer sb = new StringBuffer();
        if(file.exists()) {
            try {
                BufferedReader buf = new BufferedReader(new FileReader(path));
                String line;
                while((line=buf.readLine())!=null){
                    sb.append(line);
                }
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            listener.onError();
        }
        document = Jsoup.parse(sb.toString());
        documentItems.clear();
        listener.onLoad();
    }



    public List<Node> getOverview() {
        document.traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                if (node.nodeName().equals("title")
                        || node.nodeName().equals("h1")
                        || node.nodeName().equals("h2")
                        || node.nodeName().equals("h3")
                        || node.nodeName().equals("p")
                        || node.nodeName().equals("table")){
                    //|| node.nodeName().equals("h2")) {
                    documentItems.add(node);
                }
            }
            public void tail(Node node, int depth) {
            }
        });
        return documentItems;
    }

    public String getBodyText(int index) {
        Node node = documentItems.get(index);
//        if (node.nodeName().equals("h2")) {
        return node.childNode(0).toString();
//        } else {
//            return null;
//        }
    }
}
