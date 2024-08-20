import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.cadrikmdev.core.domain.service.BluetoothService

class AndroidBluetoothService(
    private val applicationContext: Context,
    private val bluetoothManager: BluetoothManager
): BluetoothService {

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothManager.adapter?.isEnabled == true
    }

    override fun openBluetoothSettings() {
        if (!isBluetoothEnabled()) {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            applicationContext.startActivity(intent)
        }
    }
}