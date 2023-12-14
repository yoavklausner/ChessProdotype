package Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chessprodotype.AppData;
import com.example.chessprodotype.R;
import com.example.chessprodotype.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    sign up activity. responsible for getting user data from the user and checking
    if it valids and if so then to creates new user and uploading it to the firebase.
     */

    EditText etFirstName;
    EditText etLastName;
    EditText etUserName;
    EditText etPassword, etConfirmPassword;
    Button btnSignUp, btnClear, btnCancel, btnGoToCamera, btnGoToGallery;
    ImageView ivUserImage;
    HashMap<String, User> users;
    String uName, fName, lName, pw, cpw;
    String imageUriStr = null;
    Uri imageUri = null;
    Dialog d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnClear = findViewById(R.id.btnClear);
        btnCancel = findViewById(R.id.btnCancel);
        ivUserImage = findViewById(R.id.ivUserImage);
        ivUserImage.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    //gets context in app and image bitmap
    //returns uri of wanted image
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == RESULT_OK) {
                imageUri = getImageUri(this, (Bitmap) data.getExtras().get("data"));
                imageUriStr = imageUri.toString();
            }
            else Toast.makeText(this, "camera doesn't work", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 1){
            if (resultCode == RESULT_OK){
                imageUri = data.getData();
                imageUriStr = imageUri.toString();
            }
            else Toast.makeText(this, "gallery pick doesn't work", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 2){
            if (resultCode == RESULT_OK){
                imageUriStr = data.getStringExtra("URI");
            }
            else {
                imageUriStr = null;
            }
            User user = new User(uName, pw, lName, fName, imageUriStr, User.START_RANK);
            AppData.addUser(user);
            Intent intent = new Intent();
            intent.putExtra(AppData.U_NAME, user.getUserName());
            setResult(RESULT_OK, intent);
            finish();
        }
        if (imageUri != null) {
            ivUserImage.setImageURI(imageUri);
            d.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        Button btn;
        ImageView iv;
        if (view instanceof Button) {
            btn = (Button) view;
            if (btn == btnSignUp) finishAndReturnUser();
            if (btn == btnClear) clearFields();
            if (btn == btnCancel) {
                setResult(RESULT_CANCELED);
                finish();
            }
            if (btn == btnGoToCamera){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
                d.dismiss();
            }
            if (btn == btnGoToGallery){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        }
        else if (view instanceof ImageView){
            iv = (ImageView) view;
            if (iv == ivUserImage){
                createSetImageDialog();
            }
        }
    }

    //showing dialog for setting image
    private void createSetImageDialog() {
        d = new Dialog(this);
        d.setContentView(R.layout.set_image_dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setTitle("select image source");
        btnGoToCamera = d.findViewById(R.id.btnGoToCamera);
        btnGoToGallery = d.findViewById(R.id.btnGoToGallery);
        btnGoToCamera.setOnClickListener(this);
        btnGoToGallery.setOnClickListener(this);
        d.show();
    }

    // signing up and returning the new user object via intent
    private void finishAndReturnUser(){
        if (isInputValid()) {
            if (imageUriStr != null) {
                Intent intent = new Intent(this, LoadingScreenActivity.class);
                intent.putExtra("TARGET", 1);
                intent.putExtra("URI", imageUriStr);
                startActivityForResult(intent, 2);
            }
            else{
                User user = new User(uName, pw, lName, fName, imageUriStr, User.START_RANK);
                AppData.addUser(user);
                Intent intent = new Intent();
                intent.putExtra(AppData.U_NAME, user.getUserName());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    //clearing and resetting the input views on activity
    private void clearFields(){

        etFirstName.setText("");
        etFirstName.setError(null);
        etLastName.setText("");
        etLastName.setError(null);
        etUserName.setText("");
        etUserName.setError(null);
        etPassword.setText("");
        etPassword.setError(null);
        etConfirmPassword.setText("");
        etConfirmPassword.setError(null);
        ivUserImage.setImageDrawable(this.getResources().getDrawable(R.drawable.default_user_image));
        imageUri = null;
    }

    //gets string and specific characters
    //returning true if the string contain some of the wanted characters
    private boolean isContainFrom(String str, String wantedChars){
        for (int i = 0; i < wantedChars.length(); i++)
            if (str.contains(Character.toString(wantedChars.charAt(i))))
                return true;
        return false;
    }

    //checking if the inputs in inputs view is valid to app data
    private boolean isInputValid() {
        uName = etUserName.getText().toString();
        fName = etFirstName.getText().toString();
        lName = etLastName.getText().toString();
        pw = etPassword.getText().toString();
        cpw = etConfirmPassword.getText().toString();
        String fNameError = checkNameValid(fName);
        String lNameError = checkNameValid(lName);
        String uNameError = checkNameValid(uName);
        if (fNameError != null) {
            etFirstName.setError(fNameError);
            return false;
        }
        else if (lNameError != null){
            etLastName.setError(lNameError);
            return false;
        }
        else if (uNameError != null && !uNameError.equals("contain only letters")) {
            etUserName.setError(uNameError);
            return false;
        }
        else if (AppData.isUserExist(uName)){
            etUserName.setError("user name taken try else");
            return false;
        }
        else if (pw.equals(null) || pw.length() < 8){
            etPassword.setError("invalid password, need to contain at least 8 digits");
            return false;
        }
        else if (!cpw.equals(pw)){
            etConfirmPassword.setError("make sure your confirm identify to password");
            return false;
        }
        return true;
    }

    //gets string name and checks if its valid name
    private String checkNameValid(String name){
        String badStr = "1234567890!@#$%^&*()_-+={}[]:;?/>.<,~`|";
        if (name.equals(null)) return "cant be empty";
        else if (fName.length() < 2 ) return "too short";
        else if (fName.length() > 15) return "too long";
        else if (isContainFrom(fName, badStr))return  "contain only letters";
        return null;
    }
}