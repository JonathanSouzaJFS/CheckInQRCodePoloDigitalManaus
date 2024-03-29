package br.orgipdec.checkinqrcodepolodigitalmanaus.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import br.orgipdec.checkinqrcodepolodigitalmanaus.R
import br.orgipdec.checkinqrcodepolodigitalmanaus.model.ReturnAPIIPDEC
import com.google.gson.Gson





class SharedPreferences {

    companion object {
        private val VIEW_INFO = "infoview"
        private val SET_PALESTRA = "infopalestra"
        private val SET_ORGANIZADOR = "infoorganizador"
        private val SET_SALA = "infosala"
        private val SET_DIA = "infodia"
        private val SETID_PALESTRA = "infoidpalestra"
        private val SET_JSON = "infojson"
        private val DOWNLOADONE_JSON = "infodownjson"

        fun checkDownJSON(context: Context): Boolean {
            return context
                .getSharedPreferences(
                    context.getString(R.string.downjson_file_key),
                    Context.MODE_PRIVATE
                )
                .getBoolean(DOWNLOADONE_JSON, false)
        }

        @SuppressLint("ApplySharedPref")
        fun setDownJSON(context: Context, state: Boolean) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.downjson_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putBoolean(DOWNLOADONE_JSON, state)
                commit()
            }
        }

        fun checkInfo(context: Context): Boolean {
            return context
                .getSharedPreferences(
                    context.getString(R.string.info_file_key),
                    Context.MODE_PRIVATE
                )
                .getBoolean(VIEW_INFO, false)
        }

        @SuppressLint("ApplySharedPref")
        fun setInfo(context: Context, state: Boolean) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.info_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putBoolean(VIEW_INFO, state)
                commit()
            }
        }

        fun getPalestra(context: Context) : String? {
            return context
                .getSharedPreferences(
                    context.getString(R.string.palestra_file_key),
                    Context.MODE_PRIVATE
                )
                .getString(SET_PALESTRA, null)
        }

        @SuppressLint("ApplySharedPref")
        fun setPalestra(context: Context, state: String?) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.palestra_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putString(SET_PALESTRA, state)
                commit()
            }
        }
        fun setIDPalestra(context: Context, state: String?) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.palestraid_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putString(SETID_PALESTRA, state)
                commit()
            }
        }

        fun getIDPalestra(context: Context) : String? {
            return context
                .getSharedPreferences(
                    context.getString(R.string.palestraid_file_key),
                    Context.MODE_PRIVATE
                )
                .getString(SETID_PALESTRA, null)
        }

        fun getSala(context: Context) : String? {
            return context
                .getSharedPreferences(
                    context.getString(R.string.sala_file_key),
                    Context.MODE_PRIVATE
                )
                .getString(SET_SALA, null)
        }

        @SuppressLint("ApplySharedPref")
        fun setSala(context: Context, state: String?) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.sala_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putString(SET_SALA, state)
                commit()
            }
        }
        fun getOganizador(context: Context) : String? {
            return context
                .getSharedPreferences(
                    context.getString(R.string.organizador_file_key),
                    Context.MODE_PRIVATE
                )
                .getString(SET_ORGANIZADOR, null)
        }

        @SuppressLint("ApplySharedPref")
        fun setOrganizador(context: Context, state: String?) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.organizador_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putString(SET_ORGANIZADOR, state)
                commit()
            }
        }
        fun getDia(context: Context) : String? {
            return context
                .getSharedPreferences(
                    context.getString(R.string.dia_file_key),
                    Context.MODE_PRIVATE
                )
                .getString(SET_DIA, null)
        }

        @SuppressLint("ApplySharedPref")
        fun setDia(context: Context, state: String?) {
            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.dia_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putString(SET_DIA, state)
                commit()
            }
        }

        fun getJSON(context: Context) : ReturnAPIIPDEC {

            val gson = Gson()
            val mPrefs = context.getSharedPreferences(
            context.getString(R.string.json_file_key),
            Context.MODE_PRIVATE
            )
            var json = mPrefs.getString(SET_JSON, null)
            val obj = gson.fromJson(json, ReturnAPIIPDEC::class.java)

            return obj
        }

        @SuppressLint("ApplySharedPref")


        fun setJSON(context: Context, state: ReturnAPIIPDEC?) {

            var myObject : ReturnAPIIPDEC = state!!;

            //Set the values
            val gson = Gson()
            val json = gson.toJson(myObject)

            val sharedPref = context
                .getSharedPreferences(
                    context.getString(R.string.json_file_key),
                    Context.MODE_PRIVATE
                )
            with(sharedPref.edit()) {
                putString(SET_JSON, json)
                commit()
            }
        }
    }
}