package fr.foodlens.shopping

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vuzix.sdk.barcode.BarcodeType2
import com.vuzix.sdk.barcode.ScanResult2
import com.vuzix.sdk.barcode.ScannerIntent
import fr.foodlens.R
import fr.foodlens.database.AppDatabase
import fr.foodlens.database.ShoppingListEntity
import fr.foodlens.database.ShoppingListItemEntity
import fr.foodlens.openfoodfacts.FoodApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var importShoppingListButton: Button
    private lateinit var continueShoppingButton: Button

    private val codeTypes: Array<String> = arrayOf(BarcodeType2.QR_CODE.name)
    private var cameraToggle = false

    companion object {
        private  const val REQUEST_CODE_SCAN = 9520
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_main)

        importShoppingListButton = findViewById(R.id.import_shopping_list_btn)
        continueShoppingButton = findViewById(R.id.continue_shopping_btn)

        importShoppingListButton.requestFocusFromTouch()

        importShoppingListButton.setOnClickListener {
            val scannerIntent = Intent(ScannerIntent.ACTION)
            scannerIntent.putExtra(ScannerIntent.EXTRA_BARCODE2_TYPES, codeTypes)
            if (cameraToggle) {
                scannerIntent.putExtra(ScannerIntent.EXTRA_CAMERA_ID, 0)
            } else {
                scannerIntent.putExtra(ScannerIntent.EXTRA_CAMERA_ID, 1)
            }
            cameraToggle = !cameraToggle
            try {
                startActivityForResult(scannerIntent, REQUEST_CODE_SCAN)
            } catch(_: ActivityNotFoundException) {
                Toast.makeText(this, "Only m series", Toast.LENGTH_LONG).show()
            }
        }

        continueShoppingButton.setOnClickListener {
            startActivity(Intent(this, ChooseListActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_SCAN -> {
                if (resultCode != RESULT_OK) {
                    Log.e("shopping.MainActivity", "Scan failed or cancelled by intent")
                    Toast.makeText(this, getString(R.string.scan_failed_or_cancelled), Toast.LENGTH_LONG).show()
                    return
                }
                val scanResult = data?.getParcelableExtra<ScanResult2>(ScannerIntent.RESULT_EXTRA_SCAN_RESULT2)
                // TODO Add Loader to be more user friendly
                if (scanResult != null) {
                    lifecycleScope.launch {
                        // On parse le résultat du scan qui est censé être une liste de courses
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val adapter = moshi.adapter(ShoppingListScanResult::class.java)
                        val parsedList = try {
                                            adapter.fromJson(scanResult.text)
                                        } catch (e: Exception) {
                                            Log.e("shopping.MainActivity", "Error parsing shopping list from scan result: ${e.message}")
                                            Log.e("shopping.MainActivity", "Scan result text: ${scanResult.text}")
                                            return@launch
                                        }
                        // Si le scan a échoué ou a été annulé, on affiche un message et on arrête l'exécution
                        if (parsedList == null) {
                            runOnUiThread {
                                Log.e("shopping.MainActivity", "Failed to parse shopping list from scan result: ${scanResult.text}")
                                Toast.makeText(this@MainActivity, getString(R.string.scan_failed_or_cancelled), Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }
                        // On crée la liste de courses dans la base de données
                        val shoppingList = ShoppingListEntity(label = parsedList.label)
                        val shoppingListId = AppDatabase.getDatabase(this@MainActivity).shoppingListDao().insert(shoppingList)
                        val results = parsedList.items.map {
                            async {
                                val result = FoodApiClient.getProductByCode(it)
                                result.onSuccess {
                                    return@async ShoppingListItemEntity(
                                        id = it.id,
                                        label = it.name,
                                        quantity = it.quantity,
                                        listId = shoppingListId,
                                        checked = false,
                                    )
                                }
                                null
                            }
                        }.awaitAll().filterNotNull()
                        // On insère les items de la liste de courses dans la base de données
                        try {
                            AppDatabase.getDatabase(this@MainActivity).shoppingListItemDao().insertAll(results)
                        } catch (e: Exception) {
                            // En cas d'erreur, on affiche un message et on arrête l'exécution. Ca ne devrait pas arriver normalement
                            Log.e("shopping.MainActivity", "Error inserting shopping list items: ${e.message}")
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, getString(R.string.scan_failed_or_cancelled), Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }
                        runOnUiThread {
                            startActivity(Intent(this@MainActivity, ScanActivity::class.java).putExtra("listId", shoppingListId))
                        }
                    }
                } else {
                    Log.e("shopping.MainActivity", "Scan result is null")
                    Toast.makeText(this, getString(R.string.scan_failed_or_cancelled), Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}