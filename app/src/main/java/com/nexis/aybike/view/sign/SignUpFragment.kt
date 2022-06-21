package com.nexis.aybike.view.sign

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.nexis.aybike.R
import com.nexis.aybike.model.User
import com.nexis.aybike.util.FirebaseUtils
import com.nexis.aybike.util.Singleton
import com.nexis.aybike.util.show
import com.nexis.aybike.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.util.*

class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var v: View
    private lateinit var signUpViewModel: SignUpViewModel

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dataSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var dateString: String
    private lateinit var calendar: Calendar
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    private lateinit var txtUserId: String
    private lateinit var txtUserName: String
    private lateinit var txtUserEmail: String
    private lateinit var txtUserPassword: String
    private lateinit var txtUserGender: String
    private lateinit var txtUserBirthday: String
    private lateinit var txtUserCountry: String
    private lateinit var txtUserCity: String
    private lateinit var genderArrayAdapter: ArrayAdapter<CharSequence>

    private fun init(){
        genderArrayAdapter = ArrayAdapter.createFromResource(v.context, R.array.GenderList, R.layout.spinner_item)
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sign_up_fragment_spinnerUserGender.adapter = genderArrayAdapter

        sign_up_fragment_spinnerUserGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0?.let {
                    txtUserGender = it.getItemAtPosition(p2).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                p0?.let {
                    txtUserGender = it.getItemAtPosition(0).toString()
                }
            }
        }

        sign_up_fragment_btnSignUp.setOnClickListener(this)
        sign_up_fragment_btnSignIn.setOnClickListener(this)
        sign_up_fragment_editUserBirthday.setOnClickListener(this)

        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        observeLiveData()

        initDatePicker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun initDatePicker(){
        dataSetListener = DatePickerDialog.OnDateSetListener { dataPicker, year, month, day ->
            dateString = makeDateString(day, (month + 1), year)
            sign_up_fragment_editUserBirthday.setText(dateString)
        }

        calendar = Calendar.getInstance()

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(v.context, dataSetListener, year, month, day)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    private fun makeDateString(day: Int, month: Int, year: Int) : String {
        return "${getMonthFormat(month)} $day $year"
    }

    private fun getMonthFormat(month: Int) : String {
        return when (month) {
            1 -> "OCAK"
            2 -> "ŞUBAT"
            3 -> "MART"
            4 -> "NİSAN"
            5 -> "MAYIS"
            6 -> "HAZİRAN"
            7 -> "TEMMUZ"
            8 -> "AĞUSTOS"
            9 -> "EYLÜL"
            10 -> "EKİM"
            11 -> "KASIM"
            12 -> "ARALIK"
            else -> "OCAK"
        }
    }

    private fun observeLiveData(){
        signUpViewModel.errorMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                it.show(v, it)
            }
        })

        signUpViewModel.firebaseUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                txtUserId = it.uid

                FirebaseUtils.mUser = User(
                    txtUserId,
                    txtUserName,
                    txtUserEmail,
                    txtUserGender,
                    txtUserBirthday,
                    txtUserCountry,
                    txtUserCity
                )

                signUpViewModel.saveSignUpUserData(FirebaseUtils.mUser)
            }
        })

        signUpViewModel.successMessage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                it.show(v, it)

                object : CountDownTimer(1000, 1000) {
                    override fun onTick(p0: Long) {}

                    override fun onFinish() {
                        clearAllData()
                        goToSignInPage()
                    }
                }.start()
            }
        })
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id){
                R.id.sign_up_fragment_btnSignUp -> signUpUser()
                R.id.sign_up_fragment_btnSignIn -> goToSignInPage()
                R.id.sign_up_fragment_editUserBirthday -> datePickerDialog.show()
            }
        }
    }

    private fun signUpUser(){
        txtUserName = sign_up_fragment_editUserName.text.toString().trim()
        txtUserEmail = sign_up_fragment_editUserEmail.text.toString().trim()
        txtUserPassword = sign_up_fragment_editUserPassword.text.toString().trim()
        txtUserBirthday = sign_up_fragment_editUserBirthday.text.toString().trim()
        txtUserCountry = sign_up_fragment_editUserCountry.text.toString().trim()
        txtUserCity = sign_up_fragment_editUserCity.text.toString().trim()

        if (!txtUserName.isEmpty()){
            if (!txtUserEmail.isEmpty()){
                if (!txtUserPassword.isEmpty()){
                    if (!txtUserGender.isEmpty()){
                        if (!txtUserBirthday.isEmpty()){
                            if (!txtUserCountry.isEmpty()){
                                if (!txtUserCity.isEmpty())
                                    signUpViewModel.signUpUser(txtUserEmail, txtUserPassword)
                                else
                                    txtUserCity.show(v, "Lütfen yaşadığınız şehri yazınız")
                            } else
                                txtUserCountry.show(v, "Lütfen yaşadığınız ülkeyi yazınız")
                        } else
                            txtUserBirthday.show(v, "Lütfen doğum tarihini giriniz")
                    } else
                        txtUserGender.show(v, "Lütfen listeden cinsiyetinizi seçiniz")
                } else
                    txtUserPassword.show(v, "Lütfen geçerli bir şifre belirleyiniz")
            } else
                txtUserEmail.show(v, "Lütfen geçerli bir email adresi giriniz")
        } else
            txtUserName.show(v, "Lütfen geçerli bir kullanıcı adı giriniz")
    }

    private fun goToSignInPage(){
        Singleton.showPageFromViewPager(0)
    }

    private fun clearAllData(){
        sign_up_fragment_editUserName.setText("")
        sign_up_fragment_editUserEmail.setText("")
        sign_up_fragment_editUserPassword.setText("")
        sign_up_fragment_editUserBirthday.setText("")
        sign_up_fragment_editUserCountry.setText("")
        sign_up_fragment_editUserCity.setText("")
    }
}