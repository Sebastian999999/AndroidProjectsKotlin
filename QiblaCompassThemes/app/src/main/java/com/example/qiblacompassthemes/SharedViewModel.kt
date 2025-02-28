import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qiblacompassthemes.R

class SharedViewModel : ViewModel() {
    // Use LiveData to hold the selected image resource id
    private val _selectedImageId = MutableLiveData<Int>().apply { value = R.drawable.q1compass }
    val selectedImageId: LiveData<Int> get() = _selectedImageId

    fun selectImage(imageId: Int) {
        _selectedImageId.value = imageId
    }
}
