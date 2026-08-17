package org.jedi_bachelor.blackwhitearray;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TestApplication {
    private BlackWhiteArray<Integer> array;

    private final Class<Integer> componentType = Integer.class;

    @BeforeEach
    void setUp() {
        array = new BlackWhiteArray<>(5, componentType);
    }

    @Nested
    @DisplayName("Тесты конструктора")
    class ConstructorTests {

        @Test
        @DisplayName("Создание массива с корректной ёмкостью")
        void testConstructorWithValidCapacity() {
            assertNotNull(array);
            assertEquals(0, array.size());
            assertTrue(array.isEmpty());
        }

        @Test
        @DisplayName("Создание массива с минимальной ёмкостью")
        void testConstructorWithMinCapacity() {
            BlackWhiteArray<Integer> smallArray = new BlackWhiteArray<>(1, componentType);
            assertNotNull(smallArray);
            assertEquals(0, smallArray.size());
        }

        @Test
        @DisplayName("Создание массива с разными типами")
        void testConstructorWithDifferentTypes() {
            BlackWhiteArray<String> stringArray = new BlackWhiteArray<>(5, String.class);
            assertNotNull(stringArray);

            BlackWhiteArray<Double> doubleArray = new BlackWhiteArray<>(5, Double.class);
            assertNotNull(doubleArray);
        }
    }

    // ========== ТЕСТЫ БАЗОВЫХ ОПЕРАЦИЙ ==========

    @Nested
    @DisplayName("Тесты базовых операций")
    class BasicOperationTests {

        @Test
        @DisplayName("Добавление одного элемента")
        void testAddSingleElement() {
            boolean result = array.add(10);

            assertTrue(result);
            assertEquals(1, array.size());
            assertFalse(array.isEmpty());
        }

        @Test
        @DisplayName("Добавление нескольких элементов")
        void testAddMultipleElements() {
            array.add(5);
            array.add(3);
            array.add(8);

            assertEquals(3, array.size());
        }

        @Test
        @DisplayName("Добавление null вызывает исключение")
        void testAddNullThrowsException() {
            assertThrows(NullPointerException.class, () -> array.add(null));
        }

        @Test
        @DisplayName("Проверка contains для существующего элемента")
        void testContainsExistingElement() {
            array.add(42);
            array.add(17);

            assertTrue(array.contains(42));
            assertTrue(array.contains(17));
        }

        @Test
        @DisplayName("Проверка contains для отсутствующего элемента")
        void testContainsNonExistingElement() {
            array.add(42);
            array.add(17);

            assertFalse(array.contains(99));
            assertFalse(array.contains(null));
        }

        @Test
        @DisplayName("Проверка size и isEmpty")
        void testSizeAndIsEmpty() {
            assertTrue(array.isEmpty());
            assertEquals(0, array.size());

            array.add(10);
            assertFalse(array.isEmpty());
            assertEquals(1, array.size());

            array.add(20);
            assertEquals(2, array.size());
        }
    }

    @Nested
    @DisplayName("Тесты удаления")
    class RemoveTests {
        @Test
        @DisplayName("Удаление существующего элемента")
        void testRemoveExistingElement() {
            array.add(10);
            array.add(20);
            array.add(30);

            boolean removed = array.remove(Integer.valueOf(20));

            assertTrue(removed);
            assertEquals(2, array.size());
            assertFalse(array.contains(20));
        }

        @Test
        @DisplayName("Удаление отсутствующего элемента")
        void testRemoveNonExistingElement() {
            array.add(10);
            array.add(20);

            boolean removed = array.remove(Integer.valueOf(99));

            assertFalse(removed);
            assertEquals(2, array.size());
        }

        @Test
        @DisplayName("Удаление null возвращает false")
        void testRemoveNull() {
            array.add(10);

            boolean removed = array.remove(null);

            assertFalse(removed);
            assertEquals(1, array.size());
        }

        @Test
        @DisplayName("Удаление по индексу")
        void testRemoveByIndex() {
            array.add(10);
            array.add(20);
            array.add(30);

            Integer removed = array.remove(1);

            assertEquals(10, removed);
            assertEquals(2, array.size());
            assertFalse(array.contains(10));
        }

        @Test
        @DisplayName("Удаление по индексу с выходом за границы")
        void testRemoveByIndexOutOfBounds() {
            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));

            array.add(10);
            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
        }
    }

    @Nested
    @DisplayName("Тесты компоновки")
    class LayoutTests {
        @Test
        @DisplayName("Тест компоновки до 4х")
        void testLayoutToFour() {
            array.add(1);
            array.add(2);
            array.add(3);

            assertArrayEquals(new Object[]{3}, array.getLine(0));
            assertArrayEquals(new Object[]{1, 2}, array.getLine(1));
            assertArrayEquals(new Object[]{null, null, null, null}, array.getLine(2));

            array.add(4);

            assertArrayEquals(new Object[]{null}, array.getLine(0));
            assertArrayEquals(new Object[]{null, null}, array.getLine(1));
            assertArrayEquals(new Object[]{1, 2, 3, 4}, array.getLine(2));
        }
    }

    @Nested
    @DisplayName("Тесты доступа к элементам")
    class AccessTests {
        @Test
        @DisplayName("Получение элемента с отрицательным индексом")
        void testGetWithNegativeIndex() {
            assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        }

        @Test
        @DisplayName("Получение элемента с индексом больше размера")
        void testGetWithIndexOutOfBounds() {
            array.add(10);
            assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        }

        @Test
        @DisplayName("Поиск индекса элемента")
        void testIndexOf() {
            array.add(5);
            array.add(2);
            array.add(8);
            array.add(2);

            assertEquals(2, array.indexOf(5));
            assertEquals(1, array.indexOf(2));
            assertEquals(3, array.indexOf(8));
            assertEquals(-1, array.indexOf(99));
        }

        @Test
        @DisplayName("Поиск последнего индекса элемента")
        void testLastIndexOf() {
            array.add(5);
            array.add(2);
            array.add(8);
            array.add(2);
            array.add(5);

            assertEquals(4, array.lastIndexOf(5));
            assertEquals(3, array.lastIndexOf(2));
            assertEquals(-1, array.lastIndexOf(99));
        }
    }

    @Nested
    @DisplayName("Тесты toArray")
    class ToArrayTests {
        @Test
        @DisplayName("Преобразование в Object[]")
        void testToArray() {
            array.add(10);
            array.add(20);
            array.add(30);

            Object[] result = array.toArray();

            assertArrayEquals(new Object[]{30, 10, 20}, result);
        }

        @Test
        @DisplayName("Преобразование в типизированный массив (достаточной длины)")
        void testToArrayWithSufficientLength() {
            array.add(10);
            array.add(20);
            array.add(30);

            Integer[] target = new Integer[3];
            Integer[] result = array.toArray(target);

            assertArrayEquals(new Integer[]{30, 10, 20}, result);
            assertSame(target, result);
        }

        @Test
        @DisplayName("Преобразование в типизированный массив (недостаточной длины)")
        void testToArrayWithInsufficientLength() {
            array.add(10);
            array.add(20);
            array.add(30);

            Integer[] target = new Integer[2];
            Integer[] result = array.toArray(target);

            assertArrayEquals(new Integer[]{30, 10, 20}, result);
            assertNotSame(target, result);
        }

        @Test
        @DisplayName("Преобразование в типизированный массив (большей длины)")
        void testToArrayWithGreaterLength() {
            array.add(10);
            array.add(20);

            Integer[] target = new Integer[5];
            Arrays.fill(target, 999);
            Integer[] result = array.toArray(target);

            assertEquals(10, result[0]);
            assertEquals(20, result[1]);
            assertNull(result[2]);
            assertEquals(999, result[3]);
            assertEquals(999, result[4]);
        }
    }

    @Nested
    @DisplayName("Тесты коллекционных операций")
    class CollectionOperationTests {
        @Test
        @DisplayName("containsAll с существующими элементами")
        void testContainsAllExisting() {
            array.add(10);
            array.add(20);
            array.add(30);

            List<Integer> collection = Arrays.asList(10, 20);

            assertTrue(array.containsAll(collection));
        }

        @Test
        @DisplayName("containsAll с отсутствующими элементами")
        void testContainsAllNonExisting() {
            array.add(10);
            array.add(20);

            List<Integer> collection = Arrays.asList(10, 99);

            assertFalse(array.containsAll(collection));
        }

        @Test
        @DisplayName("addAll с коллекцией")
        void testAddAll() {
            List<Integer> collection = Arrays.asList(10, 20, 30);

            boolean changed = array.addAll(collection);

            assertTrue(changed);
            assertEquals(3, array.size());
            assertArrayEquals(new Object[]{30, 10, 20}, array.toArray());
        }

        @Test
        @DisplayName("addAll с пустой коллекцией")
        void testAddAllEmpty() {
            List<Integer> collection = Collections.emptyList();

            boolean changed = array.addAll(collection);

            assertFalse(changed);
            assertEquals(0, array.size());
        }

        @Test
        @DisplayName("removeAll с коллекцией")
        void testRemoveAll() {
            array.add(10);
            array.add(20);
            array.add(30);
            array.add(40);

            List<Integer> toRemove = Arrays.asList(20, 40);

            boolean changed = array.removeAll(toRemove);

            assertTrue(changed);
            assertEquals(2, array.size());
            assertArrayEquals(new Object[]{10, 30}, array.toArray());
        }

        @Test
        @DisplayName("removeAll с отсутствующими элементами")
        void testRemoveAllNonExisting() {
            array.add(10);
            array.add(20);

            List<Integer> toRemove = Arrays.asList(99, 100);

            boolean changed = array.removeAll(toRemove);

            assertFalse(changed);
            assertEquals(2, array.size());
        }

        @Test
        @DisplayName("clear")
        void testClear() {
            array.add(10);
            array.add(20);
            array.add(30);

            array.clear();

            assertEquals(0, array.size());
            assertTrue(array.isEmpty());
            assertArrayEquals(new Object[]{}, array.toArray());
        }
    }

    @Nested
    @DisplayName("Тесты итератора")
    class IteratorTests {
        @Test
        @DisplayName("Итерация по элементам")
        void testIterator() {
            array.add(10);
            array.add(20);
            array.add(30);

            Iterator<Integer> iterator = array.iterator();

            assertTrue(iterator.hasNext());
            assertEquals(30, iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals(10, iterator.next());
            assertTrue(iterator.hasNext());
            assertEquals(20, iterator.next());
            assertFalse(iterator.hasNext());
        }

        @Test
        @DisplayName("Итератор выбрасывает исключение при next() без элементов")
        void testIteratorNextOnEmpty() {
            Iterator<Integer> iterator = array.iterator();

            assertThrows(NoSuchElementException.class, iterator::next);
        }

        @Test
        @DisplayName("ListIterator - движение вперед")
        void testListIteratorForward() {
            array.add(10);
            array.add(20);
            array.add(30);

            ListIterator<Integer> iterator = array.listIterator();

            assertTrue(iterator.hasNext());
            assertEquals(0, iterator.nextIndex());
            assertEquals(30, iterator.next());

            assertTrue(iterator.hasNext());
            assertEquals(1, iterator.nextIndex());
            assertEquals(10, iterator.next());

            assertTrue(iterator.hasNext());
            assertEquals(2, iterator.nextIndex());
            assertEquals(20, iterator.next());

            assertFalse(iterator.hasNext());
            assertEquals(3, iterator.nextIndex());
        }

        @Test
        @DisplayName("ListIterator - движение назад")
        void testListIteratorBackward() {
            array.add(10);
            array.add(20);
            array.add(30);

            ListIterator<Integer> iterator = array.listIterator(3);

            assertTrue(iterator.hasPrevious());
            assertEquals(2, iterator.previousIndex());
            assertEquals(20, iterator.previous());

            assertTrue(iterator.hasPrevious());
            assertEquals(1, iterator.previousIndex());
            assertEquals(10, iterator.previous());

            assertTrue(iterator.hasPrevious());
            assertEquals(0, iterator.previousIndex());
            assertEquals(30, iterator.previous());

            assertFalse(iterator.hasPrevious());
            assertEquals(-1, iterator.previousIndex());
        }

        /*
        @Test
        @DisplayName("ListIterator - удаление через итератор")
        void testListIteratorRemove() {
            array.add(10);
            array.add(20);
            array.add(30);

            ListIterator<Integer> iterator = array.listIterator();

            assertEquals(30, iterator.next());
            iterator.remove();

            assertEquals(2, array.size());
            assertArrayEquals(new Object[]{20, 10}, array.toArray());
        }
         */
    }

    @Nested
    @DisplayName("Стресс-тесты")
    class StressTests {
        @Test
        @DisplayName("Добавление большого количества элементов")
        void testAddManyElements() {
            int count = 100;

            for (int i = count; i > 0; i--) {
                array.add(i);
            }

            assertEquals(count, array.size());

            // Проверяем, что все элементы отсортированы
            Object[] result = array.toArray();
            for (int i = 0; i < count; i++) {
                assertEquals(i + 1, result[i]);
            }
        }

        /*
        @Test
        @DisplayName("Чередование вставок и удалений")
        void testMixAddAndRemove() {
            for (int i = 0; i < 50; i++) {
                array.add(i);
                if (i % 3 == 0 && !array.isEmpty()) {
                    array.remove(0);
                }
            }

            assertNotNull(array.toArray());
        }
         */

        /*
        @Test
        @DisplayName("Поиск после многих операций")
        void testSearchAfterManyOperations() {
            for (int i = 0; i < 50; i++) {
                array.add(i);
            }

            for (int i = 0; i < 25; i++) {
                array.remove(Integer.valueOf(i * 2));
            }

            for (int i = 100; i < 120; i++) {
                array.add(i);
            }

            assertTrue(array.contains(1));
            assertTrue(array.contains(3));
            assertFalse(array.contains(0));
            assertTrue(array.contains(100));
            assertFalse(array.contains(50));
        }
         */
    }

    @Nested
    @DisplayName("Тесты краевых случаев")
    class EdgeCaseTests {
        @Test
        @DisplayName("Работа с пустым массивом")
        void testEmptyArray() {
            assertTrue(array.isEmpty());
            assertEquals(0, array.size());
            assertFalse(array.contains(1));
            assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
            assertArrayEquals(new Object[]{}, array.toArray());
        }

        @Test
        @DisplayName("Работа с массивом из одного элемента")
        void testSingleElement() {
            array.add(42);

            assertEquals(1, array.size());
            assertEquals(42, array.get(0));
            assertTrue(array.contains(42));

            array.remove(Integer.valueOf(42));
            assertTrue(array.isEmpty());
        }

        @Test
        @DisplayName("Дубликаты элементов")
        void testDuplicates() {
            array.add(5);
            array.add(5);
            array.add(5);

            assertEquals(3, array.size());
            assertTrue(array.contains(5));
            assertEquals(0, array.indexOf(5));
            assertEquals(3, array.lastIndexOf(5));

            Object[] result = array.toArray();
            assertArrayEquals(new Object[]{5, 5, 5}, result);
        }
    }
}
