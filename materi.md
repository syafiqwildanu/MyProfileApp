# Teori 6 — Penyimpanan Data Lokal: SharedPreferences
Studi kasus: Aplikasi Pengaturan Profil | Sumber: `Teori_6.html`

## 01. Kenapa Data Bisa Hilang?

Variabel biasa hanya hidup di memori (RAM). Aplikasi ditutup -> memori dibersihkan -> data hilang.

Alur yang benar:

```
Aplikasi berjalan (variabel) --edit().apply()--> profil_prefs.xml (penyimpanan internal)
profil_prefs.xml --getString()/getInt()--> Aplikasi dibuka lagi (form terisi otomatis di onCreate)
```

| Kejadian | Variabel | SharedPreferences |
|---|---|---|
| Back keluar | Hilang | Tetap |
| Force close / swipe recent | Hilang | Tetap |
| HP mati lalu nyala | Hilang | Tetap |
| Clear data | Hilang | Terhapus |
| Uninstall | Hilang | Terhapus |

Inti: kalau data harus awet setelah tutup aplikasi, tulis ke penyimpanan.

## 02. SharedPreferences Itu Apa?

Penyimpanan key-value sederhana. Analogi label gantung: `nama`, `email`, `mode_gelap`.

Contoh isi:

| Key | Value | Tipe |
|---|---|---|
| `nama` | Rani Putri | String |
| `email` | rani@example.com | String |
| `avatar` | 1 | Int |
| `mode_gelap` | true | Boolean |

Disimpan Android sebagai satu file XML, tidak perlu kita buat manual:

```xml
<!-- /data/data/com.example.pengaturanprofil/shared_preferences/profil_prefs.xml -->
<map>
    <string name="nama">Rani Putri</string>
    <string name="email">rani@example.com</string>
    <boolean name="mode_gelap" value="true" />
    <int name="avatar" value="1" />
</map>
```

Bukti: emulator -> View > Tool Windows > Device File Explorer -> `data/data/nama.package/shared_preferences/`.

Cocok untuk: pengaturan (mode gelap, bahasa), profil singkat, flag `sudah_buka`, highscore.
Jangan untuk: gambar/video/daftar panjang, data tabel yang perlu query/sort, password/token (tidak terenkripsi, bisa dibaca di HP root). Untuk itu pakai Room / DataStore / EncryptedSharedPreferences.

## 03. Mengambil Objek & Tipe Data

```kotlin
val prefs = getSharedPreferences("profil_prefs", MODE_PRIVATE)
```

- `"profil_prefs"`: nama file, bebas, satu app boleh punya banyak file.
- `MODE_PRIVATE`: hanya app kita yang bisa baca. Selalu pakai ini.
- Alternatif: `getPreferences()` (nama file = nama Activity), `getDefaultSharedPreferences()` — untuk belajar selalu pakai `getSharedPreferences()` agar jelas.

| Tipe | Simpan | Baca | Contoh |
|---|---|---|---|
| Teks | `putString("nama","Rani")` | `getString("nama","")` | nama, email, bio |
| Int | `putInt("avatar",1)` | `getInt("avatar",0)` | avatar, skor |
| Long | `putLong("waktu",0L)` | `getLong("waktu",0L)` | waktu simpan |
| Float | `putFloat("rasio",1.5f)` | `getFloat("rasio",1f)` | ukuran huruf |
| Boolean | `putBoolean("mode_gelap",true)` | `getBoolean("mode_gelap",false)` | switch |
| Set<String> | `putStringSet("tag",setOf("a"))` | `getStringSet("tag",emptySet())` | favorit singkat |

Menulis harus lewat Editor:

```kotlin
val editor = prefs.edit()          // 1) ambil editor
editor.putString("nama","Rani Putri") // 2) isi
editor.apply()                     // 3) simpan
```

| Method | Cara kerja | Pakai kapan |
|---|---|---|
| `apply()` | async, tanpa return | Gunakan ini, tidak bikin UI macet |
| `commit()` | sync, return true/false | Hanya bila perlu cek hasil langsung |

Kesalahan paling sering: `prefs.edit().putString(...)` tanpa `apply()` -> tidak tersimpan, tanpa error.

## 04. Pola Tetap: Simpan & Baca

Simpan (3 langkah): `prefs` -> `edit() + put...` -> `apply()`.
Baca (1 langkah): `get...(KUNCI, default)` -> pakai untuk isi tampilan.

```kotlin
// SIMPAN
val prefs = getSharedPreferences("profil_prefs", MODE_PRIVATE)
prefs.edit()
    .putString("nama","Rani Putri")
    .putBoolean("mode_gelap", true)
    .apply() // jangan lupa

// BACA
val nama = prefs.getString("nama","") ?: ""
val gelap = prefs.getBoolean("mode_gelap", false)
```

`put` boleh dirantai, cukup satu `apply()` di akhir.

Default value wajib (app baru install = data kosong):

| Tipe | Default wajar |
|---|---|
| String | `""` atau `"Belum diisi"` |
| Int/Long | `0` atau `1` |
| Float | `0f` |
| Boolean | `false` |
| Set | `emptySet()` |

Kunci case-sensitive: simpan `"nama"` baca `"Nama"` -> kosong tanpa error. Solusi: `const val`.

## 05. Studi Kasus: Aplikasi Pengaturan Profil

Satu layar: kartu profil di atas + form di bawah. Semua tersimpan, dibuka lagi kembali seperti terakhir.

| Bagian layar | Key | Tipe | Awal |
|---|---|---|---|
| Nama (EditText) | `nama` | String | `""` |
| Email (EditText) | `email` | String | `""` |
| Bio (EditText) | `bio` | String | `""` |
| Avatar (1 dari 3) | `avatar` | Int 0-2 | `0` |
| Switch mode gelap | `mode_gelap` | Boolean | `false` |

Hanya 1 Activity + 1 file prefs. Tidak ada DB, tidak ada layar kedua.

Struktur:

```
PengaturanProfil/
├── app/src/main/java/.../MainActivity.kt
├── res/drawable/avatar_1.xml, avatar_2.xml, avatar_3.xml
├── res/layout/activity_main.xml
├── res/values/strings.xml
└── shared_prefs/profil_prefs.xml (dibuat otomatis saat apply() pertama, bukan manual)
```

## 06. Langkah 1-4: Project, Teks, Avatar, Layout

1. Project baru **Empty Views Activity**, nama `PengaturanProfil`, Kotlin, Min SDK API 24. Run sekali memastikan bersih.
2. `res/values/strings.xml`: `app_name=Pengaturan Profil`, `nama_default=Belum diisi`, `email_default=email@contoh.com`, `label_nama/email/bio`, `btn_ganti_avatar`, `label_mode_gelap`, `btn_simpan`, `btn_reset`, `pesan_tersimpan/reset`, `desc_avatar`.
3. Avatar vector 96dp, viewport 64x64, 3 path (lingkaran latar + kepala + bahu). Beda warna saja:

| File | Latar | Gambar |
|---|---|---|
| `avatar_1.xml` | `#dbeafe` | `#2563eb` |
| `avatar_2.xml` | `#fef3c7` | `#d97706` |
| `avatar_3.xml` | `#dcfce7` | `#15803d` |

Copy file lalu ganti 2 `fillColor`.

4. `activity_main.xml`: `ScrollView(id=layoutUtama, fillViewport=true)` > `LinearLayout vertical padding 20dp` berisi: `ivAvatar` 96dp, `tvNamaKartu` 20sp bold center, `tvEmailKartu` 14sp center, `etNama(textPersonName)`, `etEmail(textEmailAddress)`, `etBio(textMultiLine maxLines 3)`, `btnGantiAvatar`, `swGelap(SwitchCompat)`, `btnSimpan`, `btnReset`. ScrollView agar form tidak tertutup keyboard.

## 07. Langkah 5-6: Kunci & Menyimpan

```kotlin
companion object {
    private const val NAMA_PREFS = "profil_prefs"
    private const val KEY_NAMA = "nama"
    private const val KEY_EMAIL = "email"
    private const val KEY_BIO = "bio"
    private const val KEY_AVATAR = "avatar"
    private const val KEY_GELAP = "mode_gelap"
    private val DAFTAR_AVATAR = listOf(R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3)
}
private val prefs by lazy { getSharedPreferences(NAMA_PREFS, MODE_PRIVATE) }
private var indeksAvatar = 0
```

`by lazy` agar objek dibuat sekali lalu dipakai ulang.

```kotlin
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
// btnSimpan.setOnClickListener { simpanProfil() }
```

Tanpa `apply()`: UI berubah tapi setelah tutup app data kosong lagi.

## 08. Langkah 7-8: Baca, Hapus, Avatar, Mode Gelap

Baca di `onCreate` setelah `findViewById`:

```kotlin
private fun muatProfil() {
    val nama = prefs.getString(KEY_NAMA,"") ?: ""
    val email = prefs.getString(KEY_EMAIL,"") ?: ""
    val bio = prefs.getString(KEY_BIO,"") ?: ""
    indeksAvatar = prefs.getInt(KEY_AVATAR, 0)
    val gelap = prefs.getBoolean(KEY_GELAP, false)

    etNama.setText(nama)
    etEmail.setText(email)
    etBio.setText(bio)
    ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
    swGelap.isChecked = gelap

    terapkanModeGelap(gelap)
    perbaruiKartu()
}
```

Hapus semua:

```kotlin
private fun hapusSemuaData() {
    prefs.edit().clear().apply()
    Toast.makeText(this, getString(R.string.pesan_reset), Toast.LENGTH_SHORT).show()
    muatProfil()
}
// satu kunci saja: edit().remove(KEY_BIO).apply()
```

Ganti avatar (simpan hanya indeks, bukan gambar):

```kotlin
private fun gantiAvatar() {
    indeksAvatar = (indeksAvatar + 1) % DAFTAR_AVATAR.size
    ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
}
```

Mode gelap sederhana:

```kotlin
private fun terapkanModeGelap(aktif: Boolean) {
    val latar = if (aktif) "#121212" else "#FFFFFF"
    val warnaNama = if (aktif) "#F5F5F5" else "#1B1B1B"
    val warnaEmail = if (aktif) "#B0B0B0" else "#6B6B6B"
    layoutUtama.setBackgroundColor(Color.parseColor(latar))
    tvNamaKartu.setTextColor(Color.parseColor(warnaNama))
    tvEmailKartu.setTextColor(Color.parseColor(warnaEmail))
}
```

Catatan materi: untuk app nyata pakai `AppCompatDelegate.setDefaultNightMode()` + `Theme.AppCompat.DayNight`. Di sini cukup pola manual agar efek prefs terlihat.

Live update kartu:

```kotlin
etNama.doOnTextChanged { _,_,_,_ -> perbaruiKartu() }
etEmail.doOnTextChanged { _,_,_,_ -> perbaruiKartu() }
private fun perbaruiKartu() {
    tvNamaKartu.text = etNama.text.toString().trim().ifEmpty { getString(R.string.nama_default) }
    tvEmailKartu.text = etEmail.text.toString().trim().ifEmpty { getString(R.string.email_default) }
}
```

Kode lengkap `MainActivity.kt` = gabungan semua fungsi di atas, lihat `Teori_6.html` bagian 08 sebagai pembanding.

## 09. Uji Coba & Kesalahan Umum

Bukti bekerja: isi -> Simpan (`Profil tersimpan`) -> Back/tutup -> buka lagi (jam berubah) -> data utuh. Mode gelap on -> Simpan -> buka ulang langsung gelap.

Tabel uji (12): buka pertama kosong, ketik nama kartu ikut berubah, Simpan tulis XML, Back buka tetap ada, kill recent tetap ada, restart HP tetap ada, Ganti Avatar 2x + Simpan = avatar 3, mode gelap tersimpan, Reset kembali default + toast, setelah reset buka lagi tetap kosong, Clear data sistem hapus semua, cek `profil_prefs.xml` di Device File Explorer.

| Gejala | Penyebab | Solusi |
|---|---|---|
| Tidak tersimpan tanpa error | lupa `apply()` | tutup rantai dengan `.apply()` |
| Selalu kosong | kunci beda (`nama` vs `Nama`) | pakai `const val` |
| Kembali ke awal | baca variabel bukan `prefs.get...` | baca di `muatProfil()` |
| Tampil `null` | `getString()` nullable dibiarkan | `?: ""` + default |
| Lag saat Simpan | pakai `commit()` / data besar | ganti `apply()`, data besar ke Room |
| Tidak refresh UI | tidak panggil update tampilan setelah baca | panggil `perbaruiKartu()` + `terapkanModeGelap()` |
| Mau simpan gambar | prefs hanya teks/angka/boolean | simpan indeks/nama file |
| Bocor di HP root | tidak terenkripsi | jangan simpan password/token |

## 10. Ringkasan, Latihan, Tugas

Ringkasan 4: (1) Kapan: kecil/sederhana ya, besar/tabel/rahasia tidak. (2) Simpan: `getSharedPreferences + edit + put + apply`. (3) Baca: `get...(KUNCI, default)`. (4) Hapus: `remove(KUNCI)` / `clear()`.

5 salah tersering: lupa `apply()`, beda case kunci, tanpa default -> null, simpan data besar/rahasia, baca sekali padahal data berubah.

Latihan: (1) nickname 1 EditText + Simpan + tampil di TextView setelah reopen. (2) Tambah kunci `kota` lengkap simpan/baca/tampil. (3) Counter `jumlah_buka` +1 tiap buka, tampil "Kamu sudah membuka n kali".

Tugas: buat Pengaturan Profil, minimal 3 tipe (String, Int, Boolean), ada Simpan + Hapus Semua Data, kumpul screenshot sebelum/sesudah tutup + isi `profil_prefs.xml`.

Lanjutan: terstruktur -> Room, pengganti modern -> DataStore (coroutine), rahasia -> EncryptedSharedPreferences/Keystore, file media -> internal/external storage. Urutan belajar: SharedPreferences -> DataStore -> Room.

---
Status project `MyProfileApp` vs materi: `strings.xml`, `layout`, `MainActivity.kt` sudah sama persis. Perhatian: `avatar_3.xml` di project warnanya terbalik (cek Teori_6 tabel warna), dan config build masih template Compose (butuh `appcompat` + `kotlin.android` + theme AppCompat agar `AppCompatActivity` bisa run).
