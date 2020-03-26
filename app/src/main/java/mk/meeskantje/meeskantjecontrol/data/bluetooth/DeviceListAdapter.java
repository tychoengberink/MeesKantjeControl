package mk.meeskantje.meeskantjecontrol.data.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mk.meeskantje.meeskantjecontrol.R;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater inflater;
    private int viewResourseId;

    public DeviceListAdapter (Context context, int tvResourceId, ArrayList<BluetoothDevice> devices) {
        super(context, tvResourceId, devices);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewResourseId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = this.inflater.inflate(this.viewResourseId, null);

        BluetoothDevice device = this.getItem(position);

        if (device != null) {
            TextView deviceName = convertView.findViewById(R.id.textView3);
            TextView deviceAdress = convertView.findViewById(R.id.textView4);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }

            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
        }

        return convertView;
    }
}
