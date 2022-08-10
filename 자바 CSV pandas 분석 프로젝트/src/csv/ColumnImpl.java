package csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ColumnImpl implements Column {

    List<String> index = new ArrayList<String>();
    String Header;
    String Dtype;

    public ColumnImpl(Column column) { //깊은 복사를 위한 생성자
        for(int a=0; a<column.count(); a++) {
            index.add(new String(column.getValue(a)));
            Header = new String(column.getHeader());
        }
    }

    public ColumnImpl() {} //default 생성자

    public int getMaxsize() { //길이가 가장 긴 셀의 길이를 반환하는 메소드
        int Maxsize = Header.length();
        String tmp = null;
        if(isNumericColumn()&&!(isIntColumn())){
            for(int a=0; a<index.size();a++){
                try {
                    tmp = Double.toString(Math.round(Double.parseDouble(index.get(a)) * 1000000) / 1000000.0);
                    if (Maxsize < tmp.length())
                        Maxsize = tmp.length();
                }catch (NumberFormatException e) {
                    if (index.get(a).equals(""))
                        if (Maxsize < 4)
                            Maxsize = 4;
                }
            }
        }

        else {
            for (int a = 0; a < index.size(); a++) {
                if (Maxsize < index.get(a).length())
                    Maxsize = index.get(a).length();
                if (index.get(a).equals(""))
                    if (Maxsize < 4)
                        Maxsize = 4;
            }
        }
        return Maxsize;
    }

    boolean isAllString(){ //모두 string 값인지 확인하는 메소드
            for (int a = 0; a < index.size(); a++){
                if(index.get(a).equals(""))
                    continue;
             boolean isNumeric = index.get(a).matches("[+-]?\\d*(\\.\\d+)?");
             if(isNumeric==true)
                 return false;
            }

        return true;
    }

    @Override
    public String getHeader() {
        return Header;
    }

    @Override
    public String getValue(int index) {
        return this.index.get(index);
    }

    @Override
    public <T extends Number> T getValue(int index, Class<T> t) {
        try {
            if(this.index.get(index).equals(""))
                return t.cast(null);

            return t.cast(Double.parseDouble(this.index.get(index)));
        }catch (ClassCastException e){
            double doublecell = Double.parseDouble(this.index.get(index));
            int intcell = (int) doublecell; // age의 소수점이 있을 경우를 위해서
            return t.cast(intcell);
        }
    }

    @Override
    public void setValue(int index, String value) {
            this.index.set(index,value);
    }

    @Override
    public <T extends Number> void setValue(int index, T value) {

        this.index.set(index,Double.toString(value.doubleValue()));
    }

    @Override
    public int count() {
        return index.size();
    }

    @Override
    public void print() {
            int size = count();
            for(int a=0; a<size; a++){
                System.out.println(index.get(a));
            }
    }

    @Override
    public boolean isNumericColumn() {
            for(int a=0; a<index.size();a++) {
                try {
                    Double.parseDouble(index.get(a));
                } catch (NumberFormatException e) {
                    if(index.get(a).equals(""))
                        continue;
                    return false;
                }
            }

        return true;
    }

    public boolean isIntColumn() { //numeric 셀 중 int 셀인지 확인하기 위한 메소드
        try {
            for(int a=0; a<index.size();a++)
                Integer.parseInt(index.get(a));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public long getNullCount() {
        long count = 0;
        for(int a=0; a<index.size();a++)
            if(index.get(a).equals(""))
                count++;
        return count;
    }

    @Override
    public long getNumericCount() {
        long count=0;
        for(int a=0; a<index.size();a++) {
            try {
                Double.parseDouble(index.get(a));
                count=count+1;
            } catch (NumberFormatException e) {
                if(index.get(a).equals(""))
                    continue;
            }
        }
        return count;
    }

    @Override
    public double getNumericMin() {
        List<Double> numericindex = new ArrayList<Double>();
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return Collections.min(numericindex);
    }

    @Override
    public double getNumericMax() {
        List<Double> numericindex = new ArrayList<Double>();
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return Collections.max(numericindex);

    }

    @Override
    public double getMean() {
        List<Double> numericindex = new ArrayList<Double>();
        double mean;
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                    continue;
            }
        }
        double sum = 0;
        for(int a=0; a<numericindex.size();a++) {
            sum= sum + numericindex.get(a);
        }

        mean = sum/(double)numericindex.size();
        return mean ;
    }

    @Override
    public double getStd() {
        List<Double> numericindex = new ArrayList<Double>();
        double std;
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        double sum = 0;
        for(int i =0; i<numericindex.size();i++)
            sum += Math.pow(numericindex.get(i)-getMean(), 2);
        double var = sum/((double)numericindex.size()-1);
        std = Math.sqrt(var);
        return std;
    }

    @Override
    public double getQ1() {
        List<Double> numericindex = new ArrayList<Double>();
        double Q1 = 0.0;
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        Collections.sort(numericindex);
            double g= (double)(numericindex.size()-1)/4 -(numericindex.size()-1)/4 ;
            int j= (numericindex.size()-1)/4 ;
            Q1 = (1-g)*numericindex.get(j) + g*numericindex.get(j+1);

            return Q1;

    }



    @Override
    public double getMedian() {
        List<Double> numericindex = new ArrayList<Double>();
        double median;
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        Collections.sort(numericindex);
        if(numericindex.size()%2 == 0) {
            median = (numericindex.get(numericindex.size()/2-1) + numericindex.get(numericindex.size()/2) ) /(double)2;
            return median;
        }else {
            median = numericindex.get(numericindex.size()/2);
            return median;
        }
    }


    @Override
    public double getQ3() {
        List<Double> numericindex = new ArrayList<Double>();
        double Q3 = 0.0;
        for(int a=0; a<index.size();a++) {
            try {
                numericindex.add(Double.parseDouble(index.get(a)));
            } catch (NumberFormatException e) {
                continue;
            }
        }
        Collections.sort(numericindex);
        double g= (double)(numericindex.size()-1)*3/4 -(numericindex.size()-1)*3/4 ;
        int j= (numericindex.size()-1)*3/4 ;
        Q3 = (1-g)*numericindex.get(j) + g*numericindex.get(j+1);

        return Q3;
    }

    @Override
    public boolean fillNullWithMean() {
        boolean check = false;

        if(!(Dtype.equals("String")) && getNullCount()>0) {
            int indexsize = index.size();
            String mean = Double.toString(getMean());
            for (int a = 0; a < indexsize; a++){
                if(index.get(a).equals("")) {
                    index.set(a, mean);
                    check = true;
                }
            }
        }
        return check;
    }

    @Override
    public boolean fillNullWithZero() {
        boolean check = false;

        if(!(Dtype.equals("String"))&&getNullCount()>0) {
            int indexsize = index.size();
            String zero = "0";
            for (int a = 0; a < indexsize; a++){
                if(index.get(a).equals("")) {
                    index.set(a, zero);
                    check = true;
                }
            }
        }
        return check;
    }

    @Override
    public boolean standardize() {
        boolean check = false;
        if(isNumericColumn()){
            check = true;
            String tmp;
            double cell,mean,std;
            mean = getMean();
            std  = getStd();
            int size = index.size();
            for(int a=0; a<size; a++){
                if(index.get(a).equals(""))
                    continue;

                    tmp = index.get(a);
                    cell = Double.parseDouble(tmp);

                    cell = (cell - mean)/std;
                    tmp  = Double.toString(cell);

                    index.set(a,tmp);
            }
        }
        return check;
    }

    @Override
    public boolean normalize() {
        boolean check = false;
        if(isNumericColumn()){
            check = true;
            String tmp;
            double cell,min,max;
            min = getNumericMin();
            max  = getNumericMax();
            int size = index.size();
            for(int a=0; a<size; a++){
                if(index.get(a).equals(""))
                    continue;

                tmp = index.get(a);
                cell = Double.parseDouble(tmp);

                cell = (cell - min)/(max - min);
                tmp  = Double.toString(cell);

                index.set(a,tmp);
            }
        }
        return check;
    }

    public boolean isbooleancell(){
        int size = index.size();
        boolean check=true, a2check = false;
        boolean button = true;
        String a1 = null ,a2 = null;

        for(int a=0; a<size; a++){
            if(index.get(a).equals(""))
                continue;

            if(button) {
                a1 = index.get(a);
                button = false;
                continue;
            }

            if(!a1.equals(index.get(a))) {
                a2 = index.get(a);
                a2check = true;
            }

            if(a2check==true){
                if(index.get(a).equals(a1)||index.get(a).equals(a2))
                    continue;
                else
                    check = false;
            }

        }

        return check;
    }

    @Override
    public boolean factorize() {
        int size = index.size();
        boolean check=false, a2check = false;
        boolean button = true;
        String a1 = null ,a2 = null;
        if(isbooleancell()){
            check = true;
            if(isbooleancell()){
                for(int a=0; a < index.size(); a++){
                        if(index.get(a).equals(""))
                            continue;

                        if(button) {
                            a1 = index.get(a);
                            button = false;
                            index.set(a,"1");
                            continue;
                        }
                    if(a2check==false) {
                        if (!a1.equals(index.get(a))) {
                            a2 = index.get(a);
                            index.set(a,"0");
                            a2check = true;
                        }
                    }

                        if(a2check==true){
                            if(index.get(a).equals(a1))
                                index.set(a,"1");
                            else if(index.get(a).equals(a2))
                                index.set(a,"0");
                        }


                }

            }

        }

        return check;
    }
}
