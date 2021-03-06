package com.thailam.piggywallet.ui.wallet;

import android.support.annotation.NonNull;

import com.thailam.piggywallet.data.model.Wallet;
import com.thailam.piggywallet.data.source.WalletDataSource;
import com.thailam.piggywallet.data.source.base.OnDataLoadedCallback;

import java.util.List;

public class WalletPresenter implements WalletContract.Presenter,
        WalletDataSource.GetWalletCallback {
    @NonNull
    private WalletContract.View mView;
    @NonNull
    private WalletDataSource mWalletRepository;

    WalletPresenter(@NonNull WalletContract.View view,
                    @NonNull WalletDataSource walletRepository) {
        mView = view;
        mWalletRepository = walletRepository;
    }

    @Override
    public void start() {
    }

    @Override
    public void getWallets(boolean force) {
        mView.showProgressBar();
        mWalletRepository.getInitialWallets(force,this);
    }

    @Override
    public void searchWallets(String input) {
        mView.showProgressBar();
        mWalletRepository.getSearchedWallets(input, this);
    }

    @Override
    public void handleFirstSwipeRefresh() {
        if (getCachedWallets() == null || getCachedWallets().size() == 0) getWallets(false);
    }

    @Override
    public List<Wallet> getCachedWallets() {
        return mWalletRepository.getCachedWallets();
    }

    @Override
    public void deleteWallet(Wallet wallet) {
        mWalletRepository.deleteWallet(wallet, new OnDataLoadedCallback<Integer>() {
            @Override
            public void onDataLoaded(Integer data) {
                // do nothing
            }

            @Override
            public void onDataNotAvailable(Exception e) {
                mView.onGetWalletsError(e);
            }
        });
    }

    @Override
    public void onDataLoaded(List<Wallet> wallets) {
        mView.updateWallets(wallets);
        mView.hideProgressBar();
    }

    @Override
    public void onDataNotAvailable(Exception e) {
        mView.onGetWalletsError(e);
        mView.updateWallets(null);
        mView.hideProgressBar();
    }
}
