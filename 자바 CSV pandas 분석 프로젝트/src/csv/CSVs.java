package csv;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {
        TableImpl mytable = new TableImpl();
        mytable.csvpath = csv;
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(csv));
        String line = "";
        String firstLine = "";
        int rowcount = 0;
        ColumnImpl[] k = null;


        try {
            firstLine = br.readLine();
            List<String> firstaLine = new ArrayList<String>(); // 첫줄
            String[] firstlineArr = firstLine.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
            k = new ColumnImpl[firstlineArr.length];
            rowcount = firstlineArr.length;

            for (int a = 0; a < firstlineArr.length; a++) {
                k[a] = new ColumnImpl();
                if(isFirstLineHeader==false)
                    k[a].index.add(firstlineArr[a]);
                else
                    k[a].Header=firstlineArr[a];
            }

            while ((line = br.readLine()) != null) { // readLine()은 파일에서 개행된 한 줄의 데이터를 읽어온다.
                    List<String> aLine = new ArrayList<String>();
                    String[] lineArr = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1); // 파일의 한 줄을 ,로 나누어 배열에 저장 후 리스트로 변환한다.
                    for (int a = 0; a < lineArr.length; a++) {
                            lineArr[a] = lineArr[a].replaceAll("^\"|\"$",""); //앞뒤따옴표 제거
                            lineArr[a] = lineArr[a].replaceAll("\"\"","\""); //따옴표 두개 하나로
                            k[a].index.add(lineArr[a]);
                    }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            finally {
            try {
                if (br != null) {
                    br.close(); // 사용 후 BufferedReader를 닫아준다.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int a = 0; a < rowcount; a++) //Dtype 판정
            mytable.columnList.add(k[a]);
        for (int a = 0; a < rowcount; a++)
            if(mytable.columnList.get(a).isNumericColumn()){
                if (mytable.columnList.get(a).isIntColumn())
                    mytable.columnList.get(a).Dtype = "int";
                else
                    mytable.columnList.get(a).Dtype = "double";
            }
            else
                mytable.columnList.get(a).Dtype = "String";

            if(isFirstLineHeader==false){
                for(int a=0; a<mytable.columnList.size(); a++)
                    mytable.columnList.get(a).Header = " ";
            }


        return mytable;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        TableImpl mytable = new TableImpl();
        for(int a=0; a<table.getColumnCount();a++)
            mytable.columnList.add(new ColumnImpl(table.getColumn(a)));



        int i, j;
        if(isAscending==true) {
            if (mytable.columnList.get(byIndexOfColumn).isNumericColumn()) {
                for (i = mytable.columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환
                        if (mytable.Intcompare(mytable.columnList.get(byIndexOfColumn).index.get(j),mytable.columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == 1) {
                            for (int a = 0; a < mytable.columnList.size(); a++)
                                mytable.swap(j, j + 1, a);
                        }
                    }
                }
            } else {
                for (i = mytable.columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환

                        if (mytable.compare(mytable.columnList.get(byIndexOfColumn).index.get(j),mytable.columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == 1) {
                            for (int a = 0; a < mytable.columnList.size(); a++)
                                mytable.swap(j, j + 1, a);
                        }
                    }
                }
            }
        }
        else{
            if (mytable.columnList.get(byIndexOfColumn).isNumericColumn()) {
                for (i = mytable.columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환
                        if (mytable.Intcompare(mytable.columnList.get(byIndexOfColumn).index.get(j), mytable.columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == -1) {
                            for (int a = 0; a < mytable.columnList.size(); a++)
                                mytable.swap(j, j + 1, a);
                        }
                    }
                }
            } else {
                for (i = mytable.columnList.get(byIndexOfColumn).index.size() - 1; i > 0; i--) {
                    // 0 ~ (i-1)까지 반복
                    for (j = 0; j < i; j++) {
                        // j번째와 j+1번째의 요소가 크기 순이 아니면 교환

                        if (mytable.compare(mytable.columnList.get(byIndexOfColumn).index.get(j), mytable.columnList.get(byIndexOfColumn).index.get(j + 1), isNullFirst, isAscending) == -1) {
                            for (int a = 0; a < mytable.columnList.size(); a++)
                                mytable.swap(j, j + 1, a);
                        }
                    }
                }
            }
        }

       return mytable;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        TableImpl mytable = new TableImpl();
        for(int a=0; a<table.getColumnCount();a++)
            mytable.columnList.add(new ColumnImpl(table.getColumn(a)));

        List<Integer> shuffleint = new ArrayList<Integer>();// 인덱스를 섞어줄 리스트
        for(int a=0; a<mytable.columnList.get(0).count(); a++)
            shuffleint.add(a);
        Collections.shuffle(shuffleint);

        for(int a=0; a<shuffleint.size(); a++)
            for(int k=0; k<mytable.columnList.size(); k++){
                mytable.columnList.get(k).index.add(a, mytable.columnList.get(k).index.get(shuffleint.get(a) + a));
                //인자들 앞에 셔플된 891개를 추가후 원래 있던 891개는 후에 삭제 그러므로 밀리는 만큼 +a
            }
        System.out.println(mytable.columnList.get(0).index.size());
        //삭제
        int max =mytable.columnList.get(0).index.size();
        for(int a=shuffleint.size(); a<max; a++)
            for (int k = 0; k < mytable.columnList.size(); k++) {
                mytable.columnList.get(k).index.remove(shuffleint.size());
                //890번째 인자 뒤에 있는 891번 인자를 891번 삭제하여 원래 값들은 삭제
            }

        return mytable;
   }
}
