package com.osdma.milestones;

import android.graphics.Bitmap;

/**
 * @author Pranav
 * 
 */
public class ImageItem {
        private Bitmap image;
        private String title;

        public ImageItem(Bitmap image, String title) {
                super();
                this.image = image;
                this.title = title;
        }

        public Bitmap getImage() {
                return image;
        }

        public void setImage(Bitmap image) {
                this.image = image;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }
}