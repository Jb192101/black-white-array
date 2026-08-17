package org.jedi_bachelor.blackwhitearray;

import java.lang.reflect.Array;
import java.util.*;

public final class BlackWhiteArray<T extends Comparable<T>> implements List<T> {
    private List<T[]> internalArray = new ArrayList<>();

    private final Class<T> componentType;

    private final static int BASE_NUMBER = 2;

    private int currentSize = 0;

    private int currentCapacity;

    public BlackWhiteArray(int capacity, Class<T> componentType) {
        this.componentType = componentType;

        this.currentCapacity = capacity;

        int arrayCount = calculateArrayAmount(capacity);

        this.init(arrayCount);
    }

    private int calculateArrayAmount(int capacity) {
        return Integer.toBinaryString(capacity).length();
    }

    private void init(int arrayAmount) {
        for (int i = 0; i < arrayAmount; i++) {
            T[] newArray = (T[]) Array.newInstance(this.componentType, (int) Math.pow(BASE_NUMBER, i));

            this.internalArray.add(newArray);
        }
    }

    private int countAlive(T[] array) {
        if (array == null) return 0;
        int count = 0;

        for (T item : array) {
            if (item != null) count++;
        }

        return count;
    }

    private int countAliveBefore(T[] array, int position) {
        int count = 0;

        for (int i = 0; i < position && i < array.length; i++) {
            if (array[i] != null) count++;
        }

        return count;
    }

    private boolean isArrayUsed(int index) {
        return (this.currentSize & (1 << index)) != 0;
    }

    private boolean isArrayMostlyDead(T[] array) {
        return this.countAlive(array) <= array.length / 2;
    }

    private T[] merge(T[] a, T[] b) {
        T[] result = (T[]) Array.newInstance(
                this.componentType,
                a.length + b.length
        );

        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length) {
            if (a[i] == null) {
                i++;
                continue;
            }
            if (b[j] == null) {
                j++;
                continue;
            }

            if (((Comparable<T>) a[i]).compareTo(b[j]) <= 0) {
                result[k++] = a[i++];
            } else {
                result[k++] = b[j++];
            }
        }

        while (i < a.length) {
            if (a[i] != null) result[k++] = a[i];
            i++;
        }
        while (j < b.length) {
            if (b[j] != null) result[k++] = b[j];
            j++;
        }

        return result;
    }

    public T[] getLine(int index) {
        return this.internalArray.get(index);
    }

    @Override
    public int size() {
        return this.currentSize;
    }

    @Override
    public boolean isEmpty() {
        return this.currentSize == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOf(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return this.currentIndex < currentSize;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return get(this.currentIndex++);
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[this.currentSize];

        int index = 0;
        for (T[] arr : this.internalArray) {
            for (T item : arr) {
                if (arr == null) continue;
                if (item != null) {
                    array[index++] = item;
                }
            }
        }

        return array;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a.length < this.currentSize) {
            a = (T1[]) Array.newInstance(
                    a.getClass().getComponentType(),
                    this.currentSize
            );
        }

        int index = 0;
        for (T[] arr : this.internalArray) {
            for (T item : arr) {
                if (item != null) {
                    a[index++] = (T1) item;
                }
            }
        }

        if (a.length > this.currentSize) {
            a[this.currentSize] = null;
        }

        return a;
    }

    @Override
    public boolean add(T t) {
        if (t == null) throw new NullPointerException();

        int index = 0;
        T[] newElement = (T[]) Array.newInstance(this.componentType, 1);
        newElement[0] = t;

        while (true) {
            if (index >= this.internalArray.size()) {
                T[] newArray = (T[]) Array.newInstance(
                        this.componentType,
                        (int) Math.pow(BASE_NUMBER, index)
                );
                this.internalArray.add(newArray);
            }

            T[] currentArray = this.internalArray.get(index);

            if (this.isArrayEmpty(currentArray)) {
                int elementsToCopy = Math.min(newElement.length, currentArray.length);
                System.arraycopy(newElement, 0, currentArray, 0, elementsToCopy);
                this.currentSize++;
                return true;
            }

            T[] merged = merge(newElement, currentArray);
            Arrays.fill(currentArray, null);
            index++;
            newElement = merged;
        }
    }

    private boolean isArrayEmpty(T[] array) {
        if (array == null) return true;

        for (T item : array) {
            if (item != null) return false;
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;

        for (int i = 0; i < this.internalArray.size(); i++) {
            if (!this.isArrayUsed(i)) continue;

            T[] array = this.internalArray.get(i);
            if (array == null) continue;

            int index = Arrays.binarySearch(array, (T) o, Comparator.nullsLast(Comparator.naturalOrder()));

            if (index >= 0 && array[index] != null) {
                array[index] = null;
                this.currentSize--;

                if (this.isArrayMostlyDead(array)) {
                    this.demote(i);
                }

                return true;
            }
        }

        return false;
    }

    private void demote(int arrayIndex) {
        T[] arrayToDemote = this.internalArray.get(arrayIndex);
        int aliveCount = this.countAlive(arrayToDemote);

        if (aliveCount == 0) {
            this.internalArray.set(arrayIndex, null);
            return;
        }

        T[] aliveElements = (T[]) Array.newInstance(componentType, aliveCount);
        int aliveIndex = 0;
        for (T item : arrayToDemote) {
            if (item != null) {
                aliveElements[aliveIndex++] = item;
            }
        }

        Arrays.fill(arrayToDemote, null);

        int targetSize = arrayToDemote.length / 2;
        if (targetSize == 0) return;

        int targetIndex = (int) (Math.log(targetSize) / Math.log(BASE_NUMBER));

        while (targetIndex >= this.internalArray.size()) {
            T[] newArray = (T[]) Array.newInstance(
                    this.componentType,
                    (int) Math.pow(BASE_NUMBER, this.internalArray.size())
            );

            this.internalArray.add(newArray);
        }

        T[] targetArray = this.internalArray.get(targetIndex);

        if (targetArray == null || isArrayMostlyDead(targetArray)) {
            this.internalArray.set(targetIndex, aliveElements);
        } else {
            T[] merged = merge(aliveElements, targetArray);
            this.internalArray.set(targetIndex, null);
            this.internalArray.set(arrayIndex, merged);
        }
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.currentSize) {
            throw new IndexOutOfBoundsException();
        }

        int currentPos = 0;
        for (int i = 0; i < this.internalArray.size(); i++) {
            if (!this.isArrayUsed(i)) continue;

            T[] array = this.internalArray.get(i);
            if (array == null) continue;

            int aliveCount = countAlive(array);

            if (index < currentPos + aliveCount) {
                int targetInArray = index - currentPos;

                int left = 0;
                int right = array.length - 1;

                while (left <= right) {
                    int mid = (left + right) / 2;
                    int aliveBefore = countAliveBefore(array, mid);

                    if (aliveBefore == targetInArray && array[mid] != null) {
                        return array[mid];
                    } else if (aliveBefore <= targetInArray) {
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                }

                // Если бинарный поиск не нашел - линейный проход (редкий случай)
                //int aliveIndex = 0;
                //for (T item : array) {
                //    if (item != null) {
                //        if (aliveIndex == targetInArray) {
                //            return item;
                //        }
                //        aliveIndex++;
                //    }
                //}
            }

            currentPos += aliveCount;
        }

        return null;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) return -1;

        int position = 0;
        for (int i = 0; i < this.internalArray.size(); i++) {
            if (!isArrayUsed(i)) continue;

            T[] array = this.internalArray.get(i);
            if (array == null) continue;

            int idx = Arrays.binarySearch(array, (T) o);
            if (idx >= 0 && array[idx] != null) {
                int aliveBefore = 0;
                for (int j = 0; j < i; j++) {
                    if (isArrayUsed(j)) {
                        aliveBefore += countAlive(this.internalArray.get(j));
                    }
                }

                int aliveInArray = 0;
                for (int j = 0; j < idx; j++) {
                    if (array[j] != null) aliveInArray++;
                }

                return aliveBefore + aliveInArray;
            }

            position += countAlive(array);
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (o == null) return -1;

        int totalAlive = this.currentSize;
        for (int i = this.internalArray.size() - 1; i >= 0; i--) {
            if (!this.isArrayUsed(i)) continue;

            T[] array = this.internalArray.get(i);
            if (array == null) continue;

            for (int j = array.length - 1; j >= 0; j--) {
                if (array[j] != null && array[j].equals(o)) {
                    return totalAlive - this.countAliveAfter(array, j);
                }
            }

            totalAlive -= this.countAlive(array);
        }

        return -1;
    }

    private int countAliveAfter(T[] array, int position) {
        int count = 0;

        for (int i = position + 1; i < array.length; i++) {
            if (array[i] != null) count++;
        }

        return count;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!this.contains(o)) return false;
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;

        for (T item : c) {
            if (this.add(item)) modified = true;
        }

        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null || c.isEmpty()) return false;

        List<T> allElements = new ArrayList<>();
        for (T[] array : internalArray) {
            for (T item : array) {
                if (item != null) allElements.add(item);
            }
        }

        boolean changed = allElements.removeAll(c);

        if (changed) {
            this.clear();

            for (T item : allElements) {
                this.add(item);
            }
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.internalArray.size(); i++) {

            T[] array = this.internalArray.get(i);
            if (array != null) {
                Arrays.fill(array, null);
            }
        }

        currentSize = 0;
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        T value = this.get(index);

        this.remove(value);

        return value;
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new ListIterator<T>() {
            private int currentIndex = index;

            private int lastReturnedIndex = -1;

            @Override
            public boolean hasNext() {
                return this.currentIndex < currentSize;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                this.lastReturnedIndex = this.currentIndex;
                return get(this.currentIndex++);
            }

            @Override
            public boolean hasPrevious() {
                return this.currentIndex > 0;
            }

            @Override
            public T previous() {
                if (!this.hasPrevious()) throw new NoSuchElementException();

                this.lastReturnedIndex = --this.currentIndex;

                return get(this.currentIndex);
            }

            @Override
            public int nextIndex() {
                return this.currentIndex;
            }

            @Override
            public int previousIndex() {
                return this.currentIndex - 1;
            }

            @Override
            public void remove() {
                if (this.lastReturnedIndex == -1) throw new IllegalStateException();

                BlackWhiteArray.this.remove(this.lastReturnedIndex);

                this.currentIndex = this.lastReturnedIndex;

                this.lastReturnedIndex = -1;
            }

            @Override
            public void set(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(T t) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}