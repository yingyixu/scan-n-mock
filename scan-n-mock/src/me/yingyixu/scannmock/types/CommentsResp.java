
package me.yingyixu.scannmock.types;

import java.util.ArrayList;

public class CommentsResp {
    public String product;

    public ArrayList<Comment> comments;

    public class Comment {
        public String content;

        public String source;

        public User user;

        public String mid;

        public String time;

        public String pic;
    }

    public class User {
        public String uid;

        public String avatar;

        public String screen_name;
    }
}
