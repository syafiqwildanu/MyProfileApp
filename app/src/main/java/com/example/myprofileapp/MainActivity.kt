package com.example.myprofileapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NAMA_PREFS = "profil_prefs"
        private const val KEY_NAMA = "nama"
        private const val KEY_EMAIL = "email"
        private const val KEY_BIO = "bio"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_GELAP = "mode_gelap"

        private val DAFTAR_AVATAR = listOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
        )
    }

    private val prefs by lazy { getSharedPreferences(NAMA_PREFS, MODE_PRIVATE) }
    private var indeksAvatar = 0

    private lateinit var layoutUtama: View
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNamaKartu: TextView
    private lateinit var tvEmailKartu: TextView
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBio: EditText
    private lateinit var swGelap: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutUtama = findViewById(R.id.layoutUtama)
        ivAvatar = findViewById(R.id.ivAvatar)
        tvNamaKartu = findViewById(R.id.tvNamaKartu)
        tvEmailKartu = findViewById(R.id.tvEmailKartu)
        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etBio = findViewById(R.id.etBio)
        swGelap = findViewById(R.id.swGelap)

        findViewById<Button>(R.id.btnGantiAvatar).setOnClickListener { gantiAvatar() }
        findViewById<Button>(R.id.btnSimpan).setOnClickListener { simpanProfil() }
        findViewById<Button>(R.id.btnReset).setOnClickListener { hapusSemuaData() }

        etNama.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etEmail.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        swGelap.setOnCheckedChangeListener { _, aktif -> terapkanModeGelap(aktif) }

        muatProfil()
    }

    private fun simpanProfil() {
        prefs.edit()
            .putString(KEY_NAMA, etNama.text.toString().trim())
            .putString(KEY_EMAIL, etEmail.text.toString().trim())
            .putString(KEY_BIO, etBio.text.toString().trim())
            .putInt(KEY_AVATAR, indeksAvatar)
            .putBoolean(KEY_GELAP, swGelap.isChecked)
            .apply()

        Toast.makeText(this, getString(R.string.pesan_tersimpan), Toast.LENGTH_SHORT).show()
    }

    private fun muatProfil() {
        val nama = prefs.getString(KEY_NAMA, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val bio = prefs.getString(KEY_BIO, "") ?: ""
        indeksAvatar = prefs.getInt(KEY_AVATAR, 0).coerceIn(DAFTAR_AVATAR.indices)
        val gelap = prefs.getBoolean(KEY_GELAP, false)

        etNama.setText(nama)
        etEmail.setText(email)
        etBio.setText(bio)
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        swGelap.isChecked = gelap

        terapkanModeGelap(gelap)
        perbaruiKartu()
    }

    private fun hapusSemuaData() {
        prefs.edit().clear().apply()
        Toast.makeText(this, getString(R.string.pesan_reset), Toast.LENGTH_SHORT).show()
        muatProfil()
    }

    private fun gantiAvatar() {
        indeksAvatar = (indeksAvatar + 1) % DAFTAR_AVATAR.size
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
    }

    private fun perbaruiKartu() {
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        tvNamaKartu.text = nama.ifEmpty { getString(R.string.nama_default) }
        tvEmailKartu.text = email.ifEmpty { getString(R.string.email_default) }
    }

    private fun terapkanModeGelap(aktif: Boolean) {
        val latar = if (aktif) "#121212" else "#FFFFFF"
        val warnaNama = if (aktif) "#F5F5F5" else "#1B1B1B"
        val warnaEmail = if (aktif) "#B0B0B0" else "#6B6B6B"
        layoutUtama.setBackgroundColor(Color.parseColor(latar))
        tvNamaKartu.setTextColor(Color.parseColor(warnaNama))
        tvEmailKartu.setTextColor(Color.parseColor(warnaEmail))
    }
}