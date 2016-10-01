package es.barcelona.dey.memoke.presenters;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import es.barcelona.dey.memoke.beans.Pair;
import es.barcelona.dey.memoke.beans.Tab;
import es.barcelona.dey.memoke.interactors.ContentInteractor;
import es.barcelona.dey.memoke.interactors.CreationInteractor;
import es.barcelona.dey.memoke.ui.CreationActivity;
import es.barcelona.dey.memoke.ui.DialogPhoto;
import es.barcelona.dey.memoke.ui.DialogText;
import es.barcelona.dey.memoke.views.ContentView;

/**
 * Created by deyris.drake on 26/9/16.
 */
public class ContentPresenter extends ComunPresenter implements Presenter<ContentView>{

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


    public void fillPairOnView(String jsonCurrentPair){
        if(null!=jsonCurrentPair) {

          contentView.fillFirstTab();
          contentView.fillSecondTab();
          contentView.fillImgsWithCurrent();

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
}