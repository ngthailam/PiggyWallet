package com.thailam.piggywallet.ui.addtransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.thailam.piggywallet.R;
import com.thailam.piggywallet.data.model.Category;
import com.thailam.piggywallet.data.source.TransactionDataSource;
import com.thailam.piggywallet.data.source.TransactionRepository;
import com.thailam.piggywallet.data.source.local.TransactionLocalDataSource;
import com.thailam.piggywallet.ui.category.CategoryDialog;
import com.thailam.piggywallet.util.Constants;
import com.thailam.piggywallet.util.DateFormatUtils;

import java.text.ParseException;
import java.util.Calendar;

public class TransactionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        View.OnClickListener, CategoryDialog.OnCategoryChosen, TransactionContract.View {

    private TransactionContract.Presenter mPresenter;
    private DatePickerDialog mDatePickerDialog;
    private Category mCategory;
    private EditText mEditTextNote;
    private EditText mEditTextAmount;
    private Button mBtnChooseCategory, mBtnChooseDate;
    private long mDate;

    public static Intent getIntent(Context context) {
        return new Intent(context, TransactionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        initPresenter();
        initToolbar();
        initViews();
        initDatePicker();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String errMsg = getResources().getString(R.string.add_transaction_choose_date_fail);
        try {
            mDate = DateFormatUtils.getLongFromDate(year, month, dayOfMonth);
            mBtnChooseDate.setText(DateFormatUtils.getDateFromLong(mDate));
        } catch (ParseException e) {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onGetCategory(Category category) {
        mCategory = category;
        mBtnChooseCategory.setText(mCategory.getName());
    }

    @Override
    public void initPresenter() {
        if (mPresenter == null) {
            TransactionDataSource source = TransactionLocalDataSource.getInstance(getApplicationContext());
            TransactionRepository repo = TransactionRepository.getInstance(source);
            mPresenter = new TransactionPresenter(this, repo);
        }
    }

    @Override
    public void showSuccess() {
        String msg = getResources().getString(R.string.add_transaction_success);
        finish(); // move back to prev screen
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(Exception e) {
        String errMsg = e == null ? Constants.UNKNOWN_ERROR : e.getMessage();
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMissingAmountError() {
        String errMsg = getResources().getString(R.string.add_transaction_missing_amount);
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMissingCategoryError() {
        String errMsg = getResources().getString(R.string.add_transaction_missing_category);
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_add_transaction_category:
                CustomDialog dialog = new CustomDialog(this);
                dialog.show();
                break;
            case R.id.button_add_transaction_date:
                mDatePickerDialog.show();
                break;
            case R.id.button_add_transaction_save:
                handleSaveTransaction();
                break;
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_add_transaction);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(this, TransactionActivity.this,
                year, month, dayOfMonth);
    }

    private void initViews() {
        mEditTextAmount = findViewById(R.id.edit_text_add_transaction_amount);
        mEditTextNote = findViewById(R.id.edit_text_add_transaction_note);
        mBtnChooseCategory = findViewById(R.id.button_add_transaction_category);
        mBtnChooseDate = findViewById(R.id.button_add_transaction_date);
        findViewById(R.id.button_add_transaction_category).setOnClickListener(this);
        findViewById(R.id.button_add_transaction_date).setOnClickListener(this);
        findViewById(R.id.button_add_transaction_save).setOnClickListener(this);
    }

    private void handleSaveTransaction() {
        mCategory = new Category(1, "String", System.currentTimeMillis(), System.currentTimeMillis());
        String note = mEditTextNote.getText().toString();
        String amount = mEditTextAmount.getText().toString();
        mPresenter.saveTransaction(note, amount, mCategory, mDate);
    }
}