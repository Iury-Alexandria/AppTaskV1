package br.com.iuryalexandria.apptask.helper

import br.com.iuryalexandria.apptask.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    companion object {

        // retorna a referencia do BD
        fun getDatabase() = FirebaseDatabase.getInstance().reference

        //função de autenticação/ intancia do user autenticado
        fun getAuth() = FirebaseAuth.getInstance()

        // retorna o id do user atual
        fun getIdUser() = getAuth().uid

        //rotorna (true) se o user estiver autenticado no app
        fun isAutenticated() = getAuth().currentUser != null


        //validar os erros
        fun validError(error: String): Int {
            return when {
                error.contains("There is no user record corresponding to this identifier") -> {
                    R.string.account_not_registered_register_fragment
                }
                error.contains("The email address is badly formatted") -> {
                    R.string.invalid_email_register_fragment
                }
                error.contains("The password is invalid or the user does") -> {
                    R.string.invalid_password_register_fragment
                }
                error.contains("The email address is already in use by another account") -> {
                    R.string.email_in_use_register_fragment
                }
                error.contains("The given password is invalid") -> {
                    R.string.strong_password_register_fragment
                }
                error.contains("We have blocked all requests from this device due to unusual activity. Try again later.") ->{
                    R.string.exceeded_attempt_limit
                }
                else -> {
                    R.string.error_generic
                }
            }
        }


    }
}