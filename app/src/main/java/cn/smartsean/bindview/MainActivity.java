package cn.smartsean.bindview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.smartsean.annotation.BindView;

/**
 * @author smartsean
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.test)
    TextView mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cn.smartsean.api.BindView.bind(this);
        mTest.setText("This is my first annotation processor test");
    }
}
