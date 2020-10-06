package com.example.shoppinglist;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import com.example.shoppinglist.models.User;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ReadWriteIntoFile {

private static final String FILE_NAME = "example.text";

public void saveIntoFile(User u){

}

//    private static final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ciip.txt";
//    private static final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ciip.txt");
//
//    public boolean writeToFile(User data) {
//
//        ObjectOutputStream oos = null;
//        try {
//            ArrayList<User> u = readFromFile();
//
//            if (u.isEmpty()) {
//                u = new ArrayList<>();
//            }
//            u.add(data);
//
//            oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
//            oos.writeObject(u);
//            return true;
//        } catch (IOException ex) {
//            return false;
//        } finally {
//            try {
//                oos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public ArrayList<User> readFromFile() {
//        ObjectInputStream in = null;
//        ArrayList<User> users = new ArrayList<>();
//
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            in = new ObjectInputStream(new FileInputStream(path));
//
//            if (in.readObject() != null) {
//                users = (ArrayList<User>) in.readObject();
//            }
//
//        } catch (EOFException ignored) {
//        } catch (FileNotFoundException ignored) {
//        } catch (IOException ignored) {
//        } catch (ClassNotFoundException ignored) {
//        } finally {
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return users;
//    }
}