package es.barcelona.dey.memoke.presenters;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import es.barcelona.dey.memoke.beans.Pair;
import es.barcelona.dey.memoke.beans.Tab;
import es.barcelona.dey.memoke.interactors.ContentInteractor;
import es.barcelona.dey.memoke.ui.CreationActivity;
import es.barcelona.dey.memoke.ui.DialogPhoto;
import es.barcelona.dey.memoke.ui.DialogText;
import es.barcelona.dey.memoke.views.ContentView;

/**
 * Created by deyris.drake on 26/9/16.
 */
public class ContentPresenter extends ComunPresenter implements Presenter<ContentView>{

    public static final int PHOTO_FROM_GALLERY = 2;
    public static final int PHOTO_FROM_CAMERA = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_SELECT_PICTURE = 2;
    public static int finalHeight;
    public static int finalWidth;

    int mCurrentFrame;
    int mCurrentImgResultShow;
    int mCurrentTextResultShow;
    int mCurrentTab;
    String mCurrentPhotoPath;
    Pair mCurrentPair = new Pair();
    ContentView contentView;
    ContentInteractor contentInteractor;


    @Override
    public void setView(ContentView view) {
        if (view == null) throw new IllegalArgumentException("You can't set a null view");
        contentView = view;
        contentInteractor = new ContentInteractor(contentView.getContext());

    }

    @Override
    public void detachView() {
        contentView = null;
    }

    public void onViewCreated(FrameLayout  mFrameTab1,FrameLayout  mFrameTab2, Bundle savedInstanceState){
        contentView.setListenerFrame(mFrameTab1,1);
        contentView.setListenerFrame(mFrameTab2,2);

        String jsonCurrentPair = contentView.getCurrentPairFromContext(savedInstanceState);

        if(null!=jsonCurrentPair) {
            setmCurrentPair(getCurrentPair(jsonCurrentPair));
            fillPairOnView(jsonCurrentPair);

        }

        contentView.fillNumberInCurrentPair();

        //Comprobamos botones de Anterior y Siguiente
        controlButtonsAntSgte();
    }


    public void onSavingInstanceState(Bundle outState){
        //Serializamos nuestro currentPair
        boolean existCurrentPair = null!= getmCurrentPair() && getmCurrentPair().getState()!=Pair.State.EMPTY;
        if (existCurrentPair) {

            String jsonCurrentPair = getJsonCurrentPair(getmCurrentPair());
            outState.putString(CreationPresenter.PARAM_CURRENT_PAIR, jsonCurrentPair);

        }
    }

    public void creatingView(LinearLayout mLayout){
        if (mLayout!=null) {
            contentView.inicializeFragment();
        }
    }

    public void fillPairOnView(String jsonCurrentPair){
        if(null!=jsonCurrentPair) {

          contentView.fillFirstTab();
          contentView.fillSecondTab();
          contentView.fillImgsWithCurrent();

        }
    }

    public void fillHandlerWithTextAndHideImg(int textId, int position, ImageView imageView){
        final Handler handler = new Handler();
        final int finalTextId = textId;
        final int finalPosition = position;
        final ImageView finalImageView = imageView;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                contentView.fillResultWithCurrent(finalTextId, finalPosition, finalImageView);

            }
        }, 500); // after 0.5 sec
    }

    public boolean validTab(Pair mCurrentPair, int tab){
        boolean valid = false;
        if (mCurrentPair!=null){
            if (mCurrentPair.getTabs()[tab -1]!=null){
                if (mCurrentPair.getTabs()[tab -1].getType()==Tab.Type.TEXT){
                    if (mCurrentPair.getTabs()[tab -1].getText()!=null && !mCurrentPair.getTabs()[tab -1].getText().isEmpty()){
                        valid = true;
                    }
                }
                if (mCurrentPair.getTabs()[tab -1].getType()==Tab.Type.PHOTO){
                    if (null!= mCurrentPair.getTabs()[tab -1].getUri() && !mCurrentPair.getTabs()[tab -1].getUri().isEmpty()){
                        valid = true;
                    }
                }
                if(!valid){
                    mCurrentPair.getTabs()[tab -1].setState(Tab.State.IN_PROCESS);
                }
            }

        }


        return valid;
    }

    public  void validatePairStateComplete(Pair mCurrentPair){
        if (null!=mCurrentPair && !mCurrentPair.getState().equals(Pair.State.EMPTY)) {
            if (mCurrentPair.getState() != Pair.State.SAVED &&
                    (validTab(mCurrentPair,1) && validTab(mCurrentPair,2))) {
                mCurrentPair.setState(Pair.State.COMPLETED);
            }
        }
    }

    public Tab.Type getTypeById(int id){
        switch (id){
            case 0: return Tab.Type.TEXT;
            case 1: return Tab.Type.PHOTO;
            default:return Tab.Type.FIGURE;
        }
    }

    public void controlButtonsAntSgte(){
        contentView.showAntButton();
        contentView.showContinueButton();
    }

    public boolean existTextToShowInView(Pair currentPair, int tab){
        return null!= currentPair.getTabs()[tab - 1].getText() && !currentPair.getTabs()[tab - 1].getText().isEmpty();
    }

    public void fillTextInTab(Pair currentPair, int tab, int idText){
        String val = currentPair.getTabs()[tab - 1].getText();
        int size = currentPair.getTabs()[tab - 1].getSize() / 2;
        contentView.fillTextInTab(idText,val,size) ;
    }

    public void hideImageInTab(int  idText, int idImg){

        contentView.hideImageInTab(idText, idImg);
    }

    public void putTabIN_PROCESS(Pair mCurrentPair){
        if (mCurrentPair==null){
            mCurrentPair = new Pair();
        }
        mCurrentPair.setState(Pair.State.IN_PROCESS);
    }

    public void markCurrentView(View view, int tab){
       view.setTag(tab);
    }

    public int getMarkOfCurrentView(View view){
       return (int)view.getTag();
    }

    public Dialog showDialogFromFrame(Pair currentPair, int idCurrentTab, CreationActivity activity){
        if (currentPair.getTabs()[idCurrentTab].getType()==Tab.Type.TEXT) {

            DialogText textDialog = new DialogText(activity);
            if (null != currentPair.getTabs()[idCurrentTab].getText()){
                textDialog.setTextFromFragment(currentPair.getTabs()[idCurrentTab].getText());
                textDialog.setTextSizeFromFragment(currentPair.getTabs()[idCurrentTab].getSize());

            }

            return textDialog;
        }
        if (currentPair.getTabs()[idCurrentTab].getType()==Tab.Type.PHOTO) {
            DialogPhoto cdd = new DialogPhoto(activity);
            return cdd;
        }

        return null;
    }

    public File createFileFromPhoto(){
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = contentInteractor.createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }

        return photoFile;
    }


    public Pair getmCurrentPair() {
        return mCurrentPair;
    }

    public void setmCurrentPair(Pair mCurrentPair) {
        this.mCurrentPair = mCurrentPair;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public int getmCurrentTab() {
        return mCurrentTab;
    }

    public void setmCurrentTab(int mCurrentTab) {
        this.mCurrentTab = mCurrentTab;
    }

    public int getmCurrentTextResultShow() {
        return mCurrentTextResultShow;
    }

    public void setmCurrentTextResultShow(int mCurrentTextResultShow) {
        this.mCurrentTextResultShow = mCurrentTextResultShow;
    }

    public int getmCurrentImgResultShow() {
        return mCurrentImgResultShow;
    }

    public void setmCurrentImgResultShow(int mCurrentImgResultShow) {
        this.mCurrentImgResultShow = mCurrentImgResultShow;
    }

    public int getmCurrentFrame() {
        return mCurrentFrame;
    }

    public void setmCurrentFrame(int mCurrentFrame) {
        this.mCurrentFrame = mCurrentFrame;
    }
}
