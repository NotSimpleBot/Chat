import java.util.Arrays;
//КЛАСС НЕ ОТНОСИТСЯ К ОСНОВНОЙ ПРОГРАММЕ !

public class TEST_1 {
    public static void main(String[] args) {
        int[] array = {3, 1, 5, 6, 4, 2, 100, 11};
        TEST_1.sort(array, 0, array.length-1);

        System.out.println(Arrays.toString(array));
    }

    /**
     * Алгоритм сортировки слиянием.
     * <p>
     * Рекурсивный алгоритм, каждый раз массив делится на 2 примерно равные части и для каждой из
     * 2ух частей вызывается подобная рекурсия, так происходит до тех пор, пока чать массива не станет
     * неделимой.
     * <p>
     * Далее вызывается сортировка относительно делителя, похожа на сортировку вставками...Например
     * слева меньше, справа больше.
     * <p>
     */
    public static void sort(int[] array, int left, int right) {
        int delimiter = left + ((right - left) / 2) + 1;

        if (delimiter > 0 && right > (left + 1)) {
            TEST_1.sort(array, left, delimiter - 1);
            TEST_1.sort(array, delimiter, right);
        }

        int[] buffer = new int[right - left + 1];

        //темп поле, что бы не менять left при итерации
        int cursor = left;
        for (int i = 0; i < buffer.length; i++) {
            if (delimiter > right || array[cursor] > array[delimiter]) {
                buffer[i] = array[cursor];
                cursor++;
            } else {
                buffer[i] = array[delimiter];
                delimiter++;
            }
        }
        System.arraycopy(buffer, 0, array, left, buffer.length);
    }
}
