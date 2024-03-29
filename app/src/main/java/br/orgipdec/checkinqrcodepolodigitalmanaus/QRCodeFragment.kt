package br.orgipdec.checkinqrcodepolodigitalmanaus

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.orgipdec.checkinqrcodepolodigitalmanaus.data.Constants
import br.orgipdec.checkinqrcodepolodigitalmanaus.utils.SharedPreferences
import kotlinx.android.synthetic.main.qrcode_leitura.*
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import br.orgipdec.checkinqrcodepolodigitalmanaus.api.ApiErrorCredencial
import br.orgipdec.checkinqrcodepolodigitalmanaus.api.ApiServiceInterface
import br.orgipdec.checkinqrcodepolodigitalmanaus.model.RegistrarUsuario
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.login_dialog.view.*
import br.orgipdec.checkinqrcodepolodigitalmanaus.data.RUsuarioDAO
import br.orgipdec.checkinqrcodepolodigitalmanaus.data.RUsuarioRepository
import android.net.ConnectivityManager
import android.os.AsyncTask
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import io.reactivex.Single
import kotlinx.android.synthetic.main.info_dialog2.view.*
import java.net.InetSocketAddress
import java.net.Socket


class QRCodeFragment : Fragment(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null
    private val api: ApiServiceInterface = ApiServiceInterface.create()
    private val subscriptions = CompositeDisposable()
    private var contadorPalestra = 0
    private lateinit var rUsuarioDAO: RUsuarioDAO


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        HasConnection(requireActivity(), "XXX", 0).execute();
        setHasOptionsMenu(true)
        val database = Room.databaseBuilder(
            requireActivity(),
            RUsuarioRepository::class.java,
            "ipdec-database")
            .allowMainThreadQueries()
          //  .fallbackToDestructiveMigration()
            .build()
        rUsuarioDAO = database.productDao()
        return inflater.inflate(R.layout.qrcode_leitura, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        txtInfoMain.setText("Olá ${SharedPreferences.getOganizador(requireActivity())}! A Sala ${SharedPreferences.getSala(requireActivity())} está com - Participantes.")
        txtOrganization.setText("Evento: ${SharedPreferences.getPalestra(requireActivity())}")
        super.onViewCreated(view, savedInstanceState)
        mScannerView = view.findViewById(R.id.QrCodeID)
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()         // Start camera

        contarSincronizados()
        btnSincronizar.setOnClickListener{

            MostrarProgressBar()
            HasConnection(requireActivity(), "XXXX", 3).execute();

        }
    }

    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }

    override fun onPause() {
        super.onPause()
        this.cleanResult()
    }

    private fun cleanResult() {
        try {
            mScannerView!!.stopCamera() // Stop camera on pause
        } catch (e: Exception) {
            Log.e(Constants.ERROR, e.message)
        }
    }
    override fun handleResult(rawResult: Result) {

        Log.e(Constants.HANDLER, rawResult.text) // Prints scan results
        Log.e(Constants.HANDLER, rawResult.barcodeFormat.toString()) // Prints the scan format (qrcode)

        try {
            QrCodeID!!.stopCamera()

            MediaPlayer.create(requireActivity(), R.raw.sound_successful).start()

            HasConnection(requireActivity(), rawResult.text, 1).execute();


            println(Constants.STOP_CAMERA)

            // Stop camera on pause
        } catch (e: Exception) {
            Log.e(Constants.ERROR, e.message)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menuqrcode, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.cadNumber -> {

                val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.login_dialog, null)
                //AlertDialogBuilder
                val mBuilder = AlertDialog.Builder(requireActivity())
                    .setView(mDialogView)
                    .setTitle("Registrar QR Code")
                //show dialog
                val  mAlertDialog = mBuilder.show()
                mDialogView.dialogRegistrarBtn.setOnClickListener {
                    mAlertDialog.dismiss()

                    val qrcode = mDialogView.edtQRCode.text.toString()

                    MostrarProgressBar()
                    HasConnection(requireActivity(), qrcode, 1).execute();

                }

                mDialogView.dialogCancelBtn.setOnClickListener {

                    mAlertDialog.dismiss()
                }

                true
            }
            R.id.alterarConfig -> {

                MostrarProgressBar()
                HasConnection(requireActivity(), "XXX", 4).execute();

                true
            }
            R.id.alterarJSON -> {

                val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.info_dialog2, null)
                //AlertDialogBuilder
                val mBuilder = AlertDialog.Builder(requireActivity())
                    .setView(mDialogView)
                    .setTitle("Atenção!")
                //show dialog
                val  mAlertDialog = mBuilder.show()
                mDialogView.dialogRegistrarBtn2.setOnClickListener {
                    mAlertDialog.dismiss()

                    MostrarProgressBar()
                    HasConnection(requireActivity(), "XXX", 5).execute();

                }

                mDialogView.dialogCancelBtn2XX.setOnClickListener {

                    mAlertDialog.dismiss()
                }
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    fun registrarQRCode(ru : RegistrarUsuario) {

        var subscribe = api.registrarQRCode(ru).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ success ->

                HideProgressBar()
                val bundle = Bundle()
                bundle.putString("qrcode", ru.qrcode)
                bundle.putInt("contador", contadorPalestra)
                bundle.putString("nomecaboco", "404")
                findNavController().navigate(R.id.action_navigation_home_to_navigation_viewqrcode, bundle)

            }, { error ->
                HideProgressBar()
                var error2 = error
                var errorMessage = ApiErrorCredencial(error, "msg")

                if(errorMessage.message.equals("registradosucesso")) {

                    val bundle = Bundle()
                    bundle.putString("qrcode", ru.qrcode)
                    bundle.putString("nomecaboco", errorMessage.partipante)
                    bundle.putInt("contador", contadorPalestra+1)
                    findNavController().navigate(R.id.action_navigation_home_to_navigation_viewqrcode, bundle)

                }else {

                    Log.i("ResultadoJFS", "Erro: ${errorMessage.message}")
                    Toast.makeText(requireActivity(), "Erro: ${errorMessage.message}", Toast.LENGTH_SHORT)
                        .show()
                    mScannerView!!.setResultHandler(this)
                    mScannerView!!.startCamera()
                }

            })
        subscriptions.add(subscribe)
    }


    fun registrarContagem(palestra: Int) {

        var subscribe = api.getCountPalestra(palestra).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ success ->
                contadorPalestra = success.contador
                txtInfoMain.setText("Olá ${SharedPreferences.getOganizador(requireActivity())}! A Sala ${SharedPreferences.getSala(requireActivity())} está com ${contadorPalestra} Participantes.")
            }, { error ->

            })
        subscriptions.add(subscribe)
    }

    private fun contarSincronizados() {
        var teste: List<RegistrarUsuario> = rUsuarioDAO.getAll()

        if(teste.size > 0 ) {
            btnSincronizar.setText("Sincronizar COM A INTERNET (${teste.size}) ")
            btnSincronizar.setVisibility(View.VISIBLE)
        }
        else {
            btnSincronizar.setVisibility(View.INVISIBLE)
        }
    }


    fun hasInternetConnection(): Single<Boolean> {
        return Single.fromCallable {
            try {
                // Connect to Google DNS to check for connection
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)

                socket.connect(socketAddress, timeoutMs)
                socket.close()

                true
            } catch (e: IOException) {
                false
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    inner class HasConnection(private val context: Context, private val qrcode : String, private val type_info : Int) : AsyncTask<Void, Int, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnected


            if (isConnected) {
                try {
                    val urlc = URL("http://info.cern.ch/hypertext/WWW/TheProject.html")
                        .openConnection() as HttpURLConnection
                    urlc.setRequestProperty("User-Agent", "Android")
                    urlc.setRequestProperty("Connection", "close")
                    urlc.connectTimeout = 1000
                    urlc.connect()
                    if (urlc.responseCode == 204 && urlc.contentLength == 0)
                        return true

                } catch (e: IOException) {
                    Log.e("ResultadoJFS", "Error checking internet connection_HS1")
                    return false
                }

            } else {
                Log.d("ResultadoJFS", "No network available!_HS1")
                return false
            }


            return null
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)

            if(type_info == 1) {
                if (result == null || result == true) {
                    Log.d("ResultadoJFS", "COM INTERNET " + result)

                    var registrarUsuario: RegistrarUsuario = RegistrarUsuario(
                        Integer.parseInt(
                            SharedPreferences.getIDPalestra(requireActivity())!!
                        ), "${qrcode}", "${SharedPreferences.getOganizador(requireActivity())}"
                    )
                    registrarQRCode(registrarUsuario)

                } else if (result == false) {
                    Log.d("ResultadoJFS", "No network available!_HS1 >> $result")

                    var ru: RegistrarUsuario = RegistrarUsuario(
                        Integer.parseInt(
                            SharedPreferences.getIDPalestra(requireActivity())!!
                        ), "${qrcode}", "${SharedPreferences.getOganizador(requireActivity())}"
                    )
                    rUsuarioDAO.insert(ru)
                    contarSincronizados()
                    Toast.makeText(
                        requireActivity(),
                        "Internet Lenta ou Sem Conexão. Os dados foram salvos no celular.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    voltarCamera()


                }
            }else if(type_info == 3){
                if (result == null || result == true) {
                    Log.d("ResultadoJFS", "COM INTERNET " + result)

                    var infoAllUsers: List<RegistrarUsuario> = rUsuarioDAO.getAll()

                    for (usuarios in infoAllUsers) {

                        var ru : RegistrarUsuario = RegistrarUsuario(usuarios.palestra, usuarios.qrcode!!, usuarios.responsavel!!)
                        var subscribe = api.registrarQRCode(ru).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ success ->

                                rUsuarioDAO.deleteById(usuarios.id!!)

                            }, { error ->
                                var error2 = error
                                var errorMessage = ApiErrorCredencial(error, "msg")

                                if(errorMessage.message.equals("registradosucesso")) {

                                    Toast.makeText(requireActivity(), "Usuário ${usuarios.qrcode!!} Sincronizado!", Toast.LENGTH_SHORT)
                                        .show()

                                }else {
                                    Log.i("ResultadoJFS", "Erro: ${errorMessage.message}")
                                    Toast.makeText(requireActivity(), "Erro ${usuarios.qrcode!!}: ${errorMessage.message}", Toast.LENGTH_SHORT)
                                        .show()
                                    voltarCamera()
                                }
                                rUsuarioDAO.deleteById(usuarios.id!!)

                            })
                            subscriptions.add(subscribe)

                        }
                     HideProgressBar()
                    registrarContagem(Integer.parseInt(SharedPreferences.getIDPalestra(requireActivity())!!))
                    contarSincronizados()

                } else if (result == false) {
                    Toast.makeText(
                        requireActivity(),
                        "Internet Lenta ou sem Conexão no momento.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("ResultadoJFS", "No network available!_HS1 >> $result")
                }
                }else if(type_info == 4) {
                    if (result == null || result == true) {
                        SharedPreferences.setInfo(requireActivity(), false)
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()

                    } else if (result == false) {
                        Toast.makeText(requireActivity(),"Internet Lenta ou sem Conexão no momento.", Toast.LENGTH_LONG).show()
                        Log.d("ResultadoJFS", "No network available!_HS1 >> $result")
                    }
                }else if(type_info == 5) {
                    if (result == null || result == true) {
                        SharedPreferences.setInfo(requireActivity(), false)
                        SharedPreferences.setDownJSON(requireActivity(), false)
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()

                    } else if (result == false) {
                        Toast.makeText(requireActivity(),"Internet Lenta ou sem Conexão no momento.", Toast.LENGTH_LONG).show()

                        Log.d("ResultadoJFS", "No network available!_HS1 >> $result")
                    }
                }
            else{
                if (result == null || result == true) {
                    Log.d("ResultadoJFS", "COM INTERNET " + result)
                    registrarContagem(Integer.parseInt(SharedPreferences.getIDPalestra(requireActivity())!!))
                    txtInfoMain.setText("Olá ${SharedPreferences.getOganizador(requireActivity())}! A Sala ${SharedPreferences.getSala(requireActivity())} está com ${contadorPalestra} Participantes.")

                } else if (result == false) {
                    txtInfoMain.setText("Olá ${SharedPreferences.getOganizador(requireActivity())}! A Sala ${SharedPreferences.getSala(requireActivity())} está com - Participantes. ( Sem conexão com a internet )")
                    Log.d("ResultadoJFS", "No network available!_HS1 >> $result")
                }
            }
            HideProgressBar()
        }
    }
    fun MostrarProgressBar() {
        QrCodeID.setVisibility(View.INVISIBLE)
        pBarLayout.setVisibility(View.VISIBLE)
        InternetProgressBar.setVisibility(View.VISIBLE)
    }
    fun HideProgressBar() {
        QrCodeID.setVisibility(View.VISIBLE)
        pBarLayout.setVisibility(View.INVISIBLE)
        InternetProgressBar.setVisibility(View.INVISIBLE)
    }
    fun voltarCamera(){
        mScannerView!!.setResultHandler(this)
        mScannerView!!.startCamera()
    }
}
