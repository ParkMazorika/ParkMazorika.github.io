package csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

class TableImpl implements Table {
    List<ColumnImpl> columnList = new ArrayList<ColumnImpl>();
    static File csvpath;

    @Override
    public void print() {
        for (int a = 0; a < columnList.size(); a++) {
            System.out.print(" ");
            for (int k = 0; k < (columnList.get(a).getMaxsize() - columnList.get(a).Header.length()); k++) {
                System.out.print(" ");
            }
            System.out.print(columnList.get(a).Header + " |");
        }
        System.out.println();
        for (int a = 0; a < columnList.get(0).count(); a++) {
            for (int b = 0; b < columnList.size(); b++) {
                System.out.print(" ");
                    try {
                        Integer.parseInt(columnList.get(b).index.get(a));
                        if (columnList.get(b).index.get(a).equals("")) {
                            for (int k = 0; k < (columnList.get(b).getMaxsize() - 4); k++)
                                System.out.print(" ");
                        } else {
                            for (int k = 0; k < (columnList.get(b).getMaxsize() - columnList.get(b).index.get(a).length()); k++)
                                System.out.print(" ");
                        }
                    }catch (NumberFormatException e){
                        try {
                            if (columnList.get(b).index.get(a).equals("")) {
                                for (int k = 0; k < (columnList.get(b).getMaxsize() - 4); k++)
                                    System.out.print(" ");
                            } else {
                                for (int k = 0; k < (columnList.get(b).getMaxsize() - Double.toString(Math.round(Double.parseDouble(columnList.get(b).index.get(a)) * 1000000) / 1000000.0).length()); k++)
                                    System.out.print(" ");
                            }

                        }catch (NumberFormatException p){
                            if (columnList.get(b).index.get(a).equals("")) {
                                for (int k = 0; k < (columnList.get(b).getMaxsize() - 4); k++)
                                    System.out.print(" ");
                            } else {
                                for (int k = 0; k < (columnList.get(b).getMaxsize() - columnList.get(b).index.get(a).length()); k++)
                                    System.out.print(" ");
                            }
                        }
                    }

                if (columnList.get(b).index.get(a).equals(""))
                    System.out.print("null" + " |");
                else {
                    if (columnList.get(b).isNumericColumn()){
                        try {
                            System.out.print(Integer.parseInt(columnList.get(b).index.get(a)) + " |");
                        }catch (NumberFormatException e){
                            System.out.print(Math.round(Double.parseDouble(columnList.get(b).index.get(a)) * 1000000) / 1000000.0 + " |");
                        }
                    }
                    else
                        System.out.print(columnList.get(b).index.get(a) + " |");
                }
            }
            System.out.println();
        }

    }


    public String hashcode() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    @Override
    public String toString() {
        int doublecount = 0, intcount = 0, Stringcount = 0;
        String a = "<" + hashcode() + ">" + "\n";
        a = a.concat("RangeIndex: " + columnList.get(0).count() + " entries, 0 to " + (columnList.get(0).count() - 1) + "\n");
        a = a.concat("Data columns (total " + columnList.size() + " columns):" + "\n");
        a = a.concat(" # |        Column |Non-null Count |Dtype\n");
        for (int k = 0; k < columnList.size(); k++) {
            if (k < 10)
                a = a.concat(" ");
            a = a.concat(k + " |");
            for (int l = 0; 14 - columnList.get(k).Header.length() > l; l++)
                a = a.concat(" ");
            a = a.concat(columnList.get(k).Header + " |");
            a = a.concat("  " + (columnList.get(k).count() - columnList.get(k).getNullCount()) + " non-null |" + columnList.get(k).Dtype) + "\n";

            if (columnList.get(k).Dtype.equals("double"))
                doublecount++;
            else if (columnList.get(k).Dtype.equals("int"))
                intcount++;
            else
                Stringcount++;
        }
        a = a.concat("dtypes: double(" + doublecount + "), int(" + intcount + "), String(" + Stringcount + ")");
        return a;
    }

    @Override
    public Table getStats() {
        TableImpl stats = new TableImpl();
        int count = 1;
        stats.columnList.add(new ColumnImpl());
        stats.columnList.get(0).Header = " ";
        stats.columnList.get(0).index.add("count");
        stats.columnList.get(0).index.add("mean");
        stats.columnList.get(0).index.add("std");
        stats.columnList.get(0).index.add("min");
        stats.columnList.get(0).index.add("25%");
        stats.columnList.get(0).index.add("50%");
        stats.columnList.get(0).index.add("75%");
        stats.columnList.get(0).index.add("max");


        for (int a = 0; a < this.columnList.size(); a++) {
            if (columnList.get(a).isAllString())
                continue;
            else {
                stats.columnList.add(new ColumnImpl());
                stats.columnList.get(count).Header = columnList.get(a).Header;
                stats.columnList.get(count).index.add(Long.toString(columnList.get(a).getNumericCount()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getMean()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getStd()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getNumericMin()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getQ1()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getMedian()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getQ3()));
                stats.columnList.get(count).index.add(Double.toString(columnList.get(a).getNumericMax()));
                count++;
            }

        }

        return stats;
    }

    @Override
    public Table head() {
        TableImpl headtable = new TableImpl();
        for (int a = 0; a < this.columnList.size(); a++) {
            headtable.columnList.add(new ColumnImpl());
            headtable.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = 0; a < this.columnList.size(); a++) {
            for (int cell = 0; cell < 5; cell++)
                headtable.columnList.get(a).index.add(columnList.get(a).index.get(cell));

        }

        return headtable;
    }

    @Override
    public Table head(int lineCount) {
        TableImpl headtable = new TableImpl();
        for (int a = 0; a < this.columnList.size(); a++) {
            headtable.columnList.add(new ColumnImpl());
            headtable.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = 0; a < this.columnList.size(); a++) {
            for (int cell = 0; cell < lineCount; cell++)
                headtable.columnList.get(a).index.add(columnList.get(a).index.get(cell));

        }

        return headtable;
    }

    @Override
    public Table tail() {
        TableImpl tailtable = new TableImpl();
        for (int a = 0; a < this.columnList.size(); a++) {
            tailtable.columnList.add(new ColumnImpl());
            tailtable.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = 0; a < this.columnList.size(); a++) {
            for (int cell = this.columnList.get(a).index.size() - 5; cell < this.columnList.get(a).index.size(); cell++)
                tailtable.columnList.get(a).index.add(columnList.get(a).index.get(cell));

        }

        return tailtable;
    }

    @Override
    public Table tail(int lineCount) {
        TableImpl tailtable = new TableImpl();
        for (int a = 0; a < this.columnList.size(); a++) {
            tailtable.columnList.add(new ColumnImpl());
            tailtable.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = 0; a < this.columnList.size(); a++) {
            for (int cell = this.columnList.get(a).index.size() - lineCount; cell < this.columnList.get(a).index.size(); cell++)
                tailtable.columnList.get(a).index.add(columnList.get(a).index.get(cell));

        }

        return tailtable;
    }


    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        TableImpl table = new TableImpl();
        for (int a = 0; a < this.columnList.size(); a++) {
            table.columnList.add(new ColumnImpl());
            table.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = 0; a < this.columnList.size(); a++) {
            for (int cell = beginIndex; cell < endIndex; cell++)
                table.columnList.get(a).index.add(columnList.get(a).index.get(cell));

        }

        return table;
    }

    @Override
    public Table selectRowsAt(int... indices) {
        TableImpl table = new TableImpl();
        for (int a = 0; a < this.columnList.size(); a++) {
            table.columnList.add(new ColumnImpl());
            table.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = 0; a < this.columnList.size(); a++) {
            for (int cell = 0; cell < indices.length; cell++)
                table.columnList.get(a).index.add(columnList.get(a).index.get(indices[cell]));

        }

        return table;
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        TableImpl table = new TableImpl();
        for (int a = beginIndex; a < endIndex; a++) {
            table.columnList.add(new ColumnImpl());
            table.columnList.get(a).Header = columnList.get(a).Header;
        }
        for (int a = beginIndex; a < endIndex; a++) {
            for (int cell = 0; cell < columnList.get(a).index.size(); cell++)
                table.columnList.get(a).index.add(columnList.get(a).index.get(cell));

        }

        return table;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        TableImpl table = new TableImpl();
        for (int a = 0; a < indices.length; a++) {
            table.columnList.add(new ColumnImpl());
            table.columnList.get(a).Header = columnList.get(indices[a]).Header;
        }
        for (int a = 0; a < indices.length; a++) {
            for (int cell = 0; cell < columnList.get(a).index.size(); cell++)
                table.columnList.get(a).index.add(columnList.get(indices[a]).index.get(cell));

        }

        return table;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        TableImpl table = new TableImpl();
        int rightColumn = -1;
        for (int a = 0; a < columnList.size(); a++) {
            table.columnList.add(new ColumnImpl());
            table.columnList.get(a).Header = columnList.get(a).Header;
            if(columnList.get(a).Header.equals(columnName))
                rightColumn = a;

        }

            for (int cell = 0; cell < columnList.get(0).index.size(); cell++)
                for (int a = 0; a < columnList.size(); a++) {
                    if(columnList.get(rightColumn).index.get(cell).equals("")) {
                        if(predicate.test(null))
                            table.columnList.get(a).index.add(columnList.get(a).index.get(cell));
                        continue;
                    }

                    if (columnList.get(rightColumn).isNumericColumn()) {
                        try {
                            if (predicate.test((T) Double.valueOf(columnList.get(rightColumn).index.get(cell)))) {
                                table.columnList.get(a).index.add(columnList.get(a).index.get(cell));
                            }
                        }catch (ClassCastException e){
                            double doublecell = Double.parseDouble(columnList.get(rightColumn).index.get(cell));
                            int intcell = (int) doublecell; // age에 소수점이 있을 경우를 위해서
                            if (predicate.test((T) Integer.valueOf(intcell))) {
                                table.columnList.get(a).index.add(columnList.get(a).index.get(cell));
                            }
                        }
                    } else {
                        if (predicate.test((T) columnList.get(rightColumn).index.get(cell))) {
                            table.columnList.get(a).index.add(columnList.get(a).index.get(cell));
                        }
                    }
                }
        return table;
    }

    public int compare(String s1, String s2, boolean isNullFirst, boolean isAscending) {
        if (isAscending == true)
            if (isNullFirst == true) {
                if (s1.equals(""))
                    return -1;
                else if (s2.equals(""))
                    return 1;
            } else if (isNullFirst == false) {
                if (s1.equals(""))
                    return 1;
                else if (s2.equals(""))
                    return -1;
            } else {
            }
        else if (isAscending == false) {
            if (isNullFirst == false) {
                if (s1.equals(""))
                    return -1;
                else if (s2.equals(""))
                    return 1;
            } else if (isNullFirst == true) {
                if (s1.equals(""))
                    return 1;
                else if (s2.equals(""))
                    return -1;
            }
        }

        int result = 0;
        int len1 = s1.length();
        int len2 = s2.length();
        int len = (len1 < len2) ? len1 : len2;
        for (int i = 0; i < len; i++) {
            if (s1.charAt(i) > s2.charAt(i)) {
                result = 1;
                break;
            } else if (s1.charAt(i) < s2.charAt(i)) {
                result = -1;
                break;
            }
        }

        if (result == 0) {
            if (len1 > len2) {
                result = 1;
            } else if (len1 < len2) {
                result = -1;
            }
        }

        return result;

    }

    public int Intcompare(String s1, String s2, boolean isNullFirst, boolean isAscending) {

        if (isAscending == true)
            if (isNullFirst == true) {
                if (s1.equals(""))
                    return -1;
                else if (s2.equals(""))
                    return 1;
            } else if (isNullFirst == false) {
                if (s1.equals(""))
                    return 1;
                else if (s2.equals(""))
                    return -1;
            } else {
            }
        else if (isAscending == false) {
            if (isNullFirst == false) {
                if (s1.equals(""))
                    return -1;
                else if (s2.equals(""))
                    return 1;
            } else if (isNullFirst == true) {
                if (s1.equals(""))
                    return 1;
                else if (s2.equals(""))
                    return -1;
            }
        }


        int result = 0;
        try {
            double a = Double.parseDouble(s1);
            double b = Double.parseDouble(s2);
            if (a > b)
                result = 1;
            else if (a < b)
                result = -1;
        } catch (NumberFormatException e) {
            return -1;
        }

        return result;

    }

    public void swap(int index1, int index2, int columnindex) {
        String tmp = columnList.get(columnindex).index.get(index1);
        columnList.get(columnindex).index.set(index1, columnList.get(columnindex).index.get(index2));
        columnList.get(columnindex).index.set(index2, tmp);
    }


    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        int i, j;
        if (isAscending == true) {
            if (columnList.get(byIndexOfColumn).isNumericColumn()) {
                for (i = columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환
                        if (Intcompare(columnList.get(byIndexOfColumn).index.get(j), columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == 1) {
                            for (int a = 0; a < columnList.size(); a++)
                                swap(j, j + 1, a);
                        }
                    }
                }
            } else {
                for (i = columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환

                        if (compare(columnList.get(byIndexOfColumn).index.get(j), columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == 1) {
                            for (int a = 0; a < columnList.size(); a++)
                                swap(j, j + 1, a);
                        }
                    }
                }
            }
        } else {
            if (columnList.get(byIndexOfColumn).isNumericColumn()) {
                for (i = columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환
                        if (Intcompare(columnList.get(byIndexOfColumn).index.get(j), columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == -1) {
                            for (int a = 0; a < columnList.size(); a++)
                                swap(j, j + 1, a);
                        }
                    }
                }
            } else {
                for (i = columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환

                        if (compare(columnList.get(byIndexOfColumn).index.get(j), columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == -1) {
                            for (int a = 0; a < columnList.size(); a++)
                                swap(j, j + 1, a);
                        }
                    }
                }
            }
        }

        return this;
    }

    @Override
    public Table shuffle() {
        List<Integer> shuffleint = new ArrayList<Integer>();// 인덱스를 섞어줄 리스트
        for (int a = 0; a < columnList.get(0).count(); a++)
            shuffleint.add(a);
        Collections.shuffle(shuffleint);

        for (int a = 0; a < shuffleint.size(); a++)
            for (int k = 0; k < columnList.size(); k++) {
                columnList.get(k).index.add(a, columnList.get(k).index.get(shuffleint.get(a) + a));
                //인자들 앞에 셔플된 891개를 추가후 원래 있던 891개는 후에 삭제 그러므로 밀리는 만큼 +a
            }
        System.out.println(columnList.get(0).index.size());
        //삭제
        int max = columnList.get(0).index.size();
        for (int a = shuffleint.size(); a < max; a++)
            for (int k = 0; k < columnList.size(); k++) {
                columnList.get(k).index.remove(shuffleint.size());
                //890번째 인자 뒤에 있는 891번 인자를 891번 삭제하여 원래 값들은 삭제
            }

        return this;
    }

    @Override
    public int getRowCount() {
        return columnList.get(0).index.size();
    }

    @Override
    public int getColumnCount() {
        return columnList.size();
    }

    @Override
    public Column getColumn(int index) {
        return columnList.get(index);
    }

    @Override
    public Column getColumn(String name) {
        for(int a=0; a<columnList.size(); a++)
            if(columnList.get(a).getHeader().equals(name))
                return columnList.get(a);
        return null;
    }

    @Override
    public boolean fillNullWithMean() {
        int columnsize = columnList.size();
        boolean check = false;
        for (int k = 0; k < columnsize; k++) {
            if (columnList.get(k).fillNullWithMean())
                check = true;
        }

        return check;
    }


    @Override
    public boolean fillNullWithZero() {
        int columnsize = columnList.size();
        boolean check = false;

        for (int k = 0; k < columnsize; k++) {
            if (columnList.get(k).fillNullWithZero())
                check = true;
        }

        return check;
    }

    @Override
    public boolean standardize() {
        int columnsize = columnList.size();
        boolean check = false;

        for (int k = 0; k < columnsize; k++) {
            if (columnList.get(k).standardize())
                check = true;
        }
        return check;
    }

    @Override
    public boolean normalize() {
        int columnsize = columnList.size();
        boolean check = false;

        for (int k = 0; k < columnsize; k++) {
            if (columnList.get(k).normalize())
                check = true;
        }
        return check;
    }

    @Override
    public boolean factorize() {
        int columnsize = columnList.size();
        boolean check = false;

        for (int k = 0; k < columnsize; k++) {
            if (columnList.get(k).factorize())
                check = true;
        }
        return check;
    }
}
