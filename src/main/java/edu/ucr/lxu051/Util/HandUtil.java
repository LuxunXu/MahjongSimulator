package edu.ucr.lxu051.Util;

//This class is used to check if a sequence of hand is ready.

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class HandUtil {
    private int handSeq;
    private int totalNum;
    private Properties splitRule;

    public HandUtil(String handSeq) throws IOException {
        this.handSeq = Integer.valueOf(handSeq);
        int temp = this.handSeq;
        this.totalNum = 0;
        while (temp > 0) {
            this.totalNum = this.totalNum + temp % 10;
            temp = temp / 10;
        }
        this.splitRule = new Properties();
        this.splitRule.load(new FileReader("SplitRule.properties"));
    }

    public boolean reduce() {
        int handSeqCopy = this.handSeq;
        if (this.totalNum % 3 == 0) {
            return reduce3(handSeqCopy);
        }
        return true;
    }

    private boolean reduce3(int handSeq) {
        if (handSeq == 0) {
            return true;
        } else if (handSeq > 99) {
            String seq = String.valueOf(handSeq);
            int length = seq.length();
            String toSubtract = this.splitRule.getProperty(seq.substring(0, 3));
            if (toSubtract == null) return false;
            int newHandSeq = handSeq - Integer.valueOf(toSubtract) * (int) Math.pow(10, length - 3);
            return reduce3(newHandSeq);
        } else if (handSeq < 10) {
            String seq = String.valueOf(handSeq);
            int length = seq.length();
            String toSubtract = this.splitRule.getProperty(seq);
            if (toSubtract == null) return false;
            int newHandSeq = handSeq - Integer.valueOf(toSubtract);
            return reduce3(newHandSeq);
        } else {
            String seq = String.valueOf(handSeq);
            int length = seq.length();
            String toSubtract = this.splitRule.getProperty(seq.substring(0, 2));
            if (toSubtract == null) return false;
            int newHandSeq = handSeq - Integer.valueOf(toSubtract) * (int) Math.pow(10, length - 2);
            return reduce3(newHandSeq);
        }
    }
}
