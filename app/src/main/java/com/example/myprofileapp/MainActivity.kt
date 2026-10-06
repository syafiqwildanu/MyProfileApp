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
        private const val KEY_KOTA = "kota"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_GELAP = "mode_gelap"
        private const val KEY_JUMLAH_BUKA = "jumlah_buka"

        private val DAFTAR_AVATAR = listOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
        )
    }

    private val prefs by lazy { getSharedPreferences(NAMA_PREFS, MODE_PRIVATE) }
    private var indeksAvatar = 0

    private lateinit var layoutUtama: View
    private lateinit var tvAppBar: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNamaKartu: TextView
    private lateinit var tvEmailKartu: TextView
    private lateinit var tvKotaKartu: TextView
    private lateinit var tvLabelAvatar: TextView
    private lateinit var ivAvatar1: ImageView
    private lateinit var ivAvatar2: ImageView
    private lateinit var ivAvatar3: ImageView
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etBio: EditText
    private lateinit var etKota: EditText
    private lateinit var swGelap: SwitchCompat
    private lateinit var tvJumlahBuka: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutUtama = findViewById(R.id.layoutUtama)
        tvAppBar = findViewById(R.id.tvAppBar)
        ivAvatar = findViewById(R.id.ivAvatar)
        tvNamaKartu = findViewById(R.id.tvNamaKartu)
        tvEmailKartu = findViewById(R.id.tvEmailKartu)
        tvKotaKartu = findViewById(R.id.tvKotaKartu)
        tvLabelAvatar = findViewById(R.id.tvLabelAvatar)
        ivAvatar1 = findViewById(R.id.ivAvatar1)
        ivAvatar2 = findViewById(R.id.ivAvatar2)
        ivAvatar3 = findViewById(R.id.ivAvatar3)
        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        etBio = findViewById(R.id.etBio)
        etKota = findViewById(R.id.etKota)
        swGelap = findViewById(R.id.swGelap)
        tvJumlahBuka = findViewById(R.id.tvJumlahBuka)

        findViewById<Button>(R.id.btnGantiAvatar).setOnClickListener { gantiAvatar() }
        findViewById<Button>(R.id.btnSimpan).setOnClickListener { simpanProfil() }
        findViewById<Button>(R.id.btnReset).setOnClickListener { hapusSemuaData() }

        ivAvatar1.setOnClickListener { pilihAvatar(0) }
        ivAvatar2.setOnClickListener { pilihAvatar(1) }
        ivAvatar3.setOnClickListener { pilihAvatar(2) }

        etNama.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etEmail.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etKota.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        swGelap.setOnCheckedChangeListener { _, aktif -> terapkanModeGelap(aktif) }

        muatProfil()
    }

    private fun simpanProfil() {
        prefs.edit()
            .putString(KEY_NAMA, etNama.text.toString().trim())
            .putString(KEY_EMAIL, etEmail.text.toString().trim())
            .putString(KEY_BIO, etBio.text.toString().trim())
            .putString(KEY_KOTA, etKota.text.toString().trim())
            .putInt(KEY_AVATAR, indeksAvatar)
            .putBoolean(KEY_GELAP, swGelap.isChecked)
            .apply()

        Toast.makeText(this, getString(R.string.pesan_tersimpan), Toast.LENGTH_SHORT).show()
    }

    private fun muatProfil() {
        val nama = prefs.getString(KEY_NAMA, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val bio = prefs.getString(KEY_BIO, "") ?: ""
        val kota = prefs.getString(KEY_KOTA, "") ?: ""
        indeksAvatar = prefs.getInt(KEY_AVATAR, 0).coerceIn(DAFTAR_AVATAR.indices)
        val gelap = prefs.getBoolean(KEY_GELAP, false)

        // Penghitung pembukaan aplikasi (direset ikut clear karena satu file prefs)
        val jumlahBuka = prefs.getInt(KEY_JUMLAH_BUKA, 0) + 1
        prefs.edit().putInt(KEY_JUMLAH_BUKA, jumlahBuka).apply()

        etNama.setText(nama)
        etEmail.setText(email)
        etBio.setText(bio)
        etKota.setText(kota)
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        perbaruiPenandaAvatar()
        swGelap.isChecked = gelap
        tvJumlahBuka.text = getString(R.string.info_buka, jumlahBuka)

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
        perbaruiPenandaAvatar()
    }

    private fun pilihAvatar(indeks: Int) {
        indeksAvatar = indeks.coerceIn(DAFTAR_AVATAR.indices)
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
        perbaruiPenandaAvatar()
    }

    private fun perbaruiPenandaAvatar() {
        val previews = listOf(ivAvatar1, ivAvatar2, ivAvatar3)
        previews.forEachIndexed { i, iv ->
            iv.setBackgroundResource(
                if (i == indeksAvatar) R.drawable.ring_avatar_selected
                else R.drawable.ring_avatar_normal
            )
        }
    }

    private fun perbaruiKartu() {
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val kota = etKota.text.toString().trim()
        tvNamaKartu.text = nama.ifEmpty { getString(R.string.nama_default) }
        tvEmailKartu.text = email.ifEmpty { getString(R.string.email_default) }
        tvKotaKartu.text = kota.ifEmpty { getString(R.string.kota_default) }
    }

    private fun terapkanModeGelap(aktif: Boolean) {
        val latar = if (aktif) "#121212" else "#FFFFFF"
        val warnaNama = if (aktif) "#F5F5F5" else "#1B1B1B"
        val warnaEmail = if (aktif) "#B0B0B0" else "#6B6B6B"
        val warnaInput = if (aktif) "#E5E5E5" else "#334155"
        val warnaHint = if (aktif) "#9CA3AF" else "#94A3B8"
        val warnaLabel = if (aktif) "#9CA3AF" else "#64748B"
        val appBarBg = if (aktif) "#14532D" else "#2DB86F"
        val appBarText = if (aktif) "#D1FAE5" else "#08301C"

        layoutUtama.setBackgroundColor(Color.parseColor(latar))
        tvNamaKartu.setTextColor(Color.parseColor(warnaNama))
        tvEmailKartu.setTextColor(Color.parseColor(warnaEmail))
        tvKotaKartu.setTextColor(Color.parseColor(warnaEmail))
        tvJumlahBuka.setTextColor(Color.parseColor(warnaEmail))
        tvLabelAvatar.setTextColor(Color.parseColor(warnaLabel))
        swGelap.setTextColor(Color.parseColor(warnaInput))
        tvAppBar.setBackgroundColor(Color.parseColor(appBarBg))
        tvAppBar.setTextColor(Color.parseColor(appBarText))

        // Perbaikan bug: teks & hint input ikut menyesuaikan mode agar tetap terbaca
        listOf(etNama, etEmail, etBio, etKota).forEach {
            it.setTextColor(Color.parseColor(warnaInput))
            it.setHintTextColor(Color.parseColor(warnaHint))
        }
    }
}
