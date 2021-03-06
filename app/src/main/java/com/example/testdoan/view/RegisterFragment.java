package com.example.testdoan.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testdoan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button signup;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;


    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_register, container, false);

        EditText txtemail = v.findViewById(R.id.email_register);
        EditText txtpass = v.findViewById(R.id.password_register);
        signup = v.findViewById(R.id.signupacccount);
        mAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtemail.getText().toString();
                String password=txtpass.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("xx", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    getFragmentManager().popBackStack();
                                    Toast.makeText(getContext(), "Success",
                                            Toast.LENGTH_SHORT).show();

                                    //  updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("xx", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });


        return v ;
    }
}