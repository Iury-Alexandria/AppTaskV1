package br.com.iuryalexandria.apptask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.FragmentHomeBinding
import br.com.iuryalexandria.apptask.ui.adapter.ViewPagerAdapeter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        configTabLayout()

        initClicks()
    }

    // configurações do tablayout com os viewPages
    private fun configTabLayout() {

        val objAdapter = ViewPagerAdapeter(requireActivity())
        val viewPager = binding.viewPager
        viewPager.adapter = objAdapter

        objAdapter.getFragment(TodoFragment(), "A Fazer")
        objAdapter.getFragment(DoingFragment(), "Fazendo")
        objAdapter.getFragment(DoneFragment(), "Feitas")

        viewPager.offscreenPageLimit = objAdapter.itemCount

        val mediator: TabLayoutMediator =
            TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.setText(objAdapter.getTitle(position))
            }
        mediator.attach()
    }

    //iniciar cliques
    private fun initClicks(){
        binding.ibLogout.setOnClickListener {logoutUser() }
    }

    private fun logoutUser(){
        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_autentication)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}