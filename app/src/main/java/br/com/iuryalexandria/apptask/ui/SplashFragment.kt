package br.com.iuryalexandria.apptask.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentSplashBinding
import br.com.iuryalexandria.apptask.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vai durar 3 seg e levar pra outra tela
        Handler(Looper.getMainLooper()).postDelayed(this::checkAuth,3000)
    }


    private fun checkAuth(){
        val auth = FirebaseHelper.getAuth()
        if (auth.currentUser == null){
            //primeiro acesso
            findNavController().navigate(R.id.action_splashFragment_to_autentication)
        }else{
            //se já estiver conectado
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}