package rtlsdr.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RtlSdrService extends Service {
    public RtlSdrService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
