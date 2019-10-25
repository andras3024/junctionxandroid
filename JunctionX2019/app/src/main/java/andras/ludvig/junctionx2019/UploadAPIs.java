package andras.ludvig.junctionx2019;

import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import okhttp3.MultipartBody;
import retrofit2.http.Url;

public interface UploadAPIs {
    @Multipart
    @POST
    Call<ResponseBody> uploadImage(@Url String url, @Part MultipartBody.Part file, @Part("name") RequestBody requestBody, @Header("Authorization") String authorization);
}

