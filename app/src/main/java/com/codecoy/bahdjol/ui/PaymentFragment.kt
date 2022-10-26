package com.codecoy.bahdjol.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.AddPaymentDialogBinding
import com.codecoy.bahdjol.databinding.FragmentPaymentBinding
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.datamodels.WalletData
import com.codecoy.bahdjol.datamodels.WalletResponse
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class PaymentFragment : Fragment() {

    private lateinit var myViewModel: MyViewModel
    private var userData: UserData? = null

    private lateinit var mBinding: FragmentPaymentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPaymentBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        getUserData()


        mBinding.btnNewPayment.setOnClickListener {

            showDialog()

        }

    }

    private fun showDialog() {

        val addBalanceBinding: AddPaymentDialogBinding =
            AddPaymentDialogBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireActivity())
        dialog.setContentView(addBalanceBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.30).toInt()

        dialog.window?.setLayout(width, height)

        addBalanceBinding.btnNewPayment.setOnClickListener {

            val balanceCode = addBalanceBinding.etCode.text.trim().toString()

            if (balanceCode.isEmpty()){
                addBalanceBinding.etCode.error = "Code is required!"
                addBalanceBinding.etCode.requestFocus()
            } else {
                dialog.dismiss()
                addBalance(balanceCode)
            }

        }

        dialog.show()

    }

    private fun addBalance(balanceCode: String) {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        if (userData != null){

            myViewModel.addBalance(userData!!.id!!, balanceCode)

            myViewModel.addBalanceLiveData.observe(viewLifecycleOwner
            ) {

                dialog.dismiss()
                if (it.status == true && it.data != null) {

                    Log.i(Constant.TAG, "response: add success ${it.data!!.balance}")

                    val walletData = it.data

                    val currentBalance: Double = walletData?.balance!!.toDouble()

                    mBinding.tvBalance.text = currentBalance.toString()

                    ServiceIds.saveBalanceIntoPref(requireActivity(), "balanceInfo", currentBalance.toString())

                }else {
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                }

            }

        }

    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(requireActivity(), "userInfo")

        if (userData != null) {

            userBalance(userData!!)

        }

    }

    private fun userBalance(userData: UserData) {
        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.userBalance(userData.id!!)

        myViewModel.userBalanceLiveData.observe(viewLifecycleOwner
        ) {
            dialog.dismiss()
            if (it.status == true && it.data != null) {
                Log.i(Constant.TAG, "response: payment success ${it.data!!.balance}")

                val walletData = it.data

                Glide.with(requireActivity()).load(Constant.IMG_URL + walletData!!.profileImg)
                    .placeholder(R.drawable.ic_downloading)
                    .error(R.drawable.ic_error)
                    .into(mBinding.ivProfile)

                mBinding.tvName.text = walletData.name

                val currentBalance: Double = walletData.balance!!.toDouble()

                mBinding.tvBalance.text = currentBalance.toString()

                ServiceIds.saveBalanceIntoPref(requireActivity(), "balanceInfo", currentBalance.toString())

            }else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

}