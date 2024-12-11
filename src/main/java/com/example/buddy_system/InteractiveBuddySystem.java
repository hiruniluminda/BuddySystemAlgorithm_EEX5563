package com.example.buddy_system;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InteractiveBuddySystem {
    private static final int MEMORY_SIZE = 1024; // Total memory size in KB
    private final List<MemoryBlock> memoryBlocks;

    public InteractiveBuddySystem() {
        memoryBlocks = new ArrayList<>();
        memoryBlocks.add(new MemoryBlock(0, MEMORY_SIZE, true)); // Initialize memory pool
    }

    public static void main(String[] args) {
        InteractiveBuddySystem buddySystem = new InteractiveBuddySystem();
        buddySystem.run();
    }

    private void run() {
        System.out.println("Buddy System Initialized with " + MEMORY_SIZE + " KB.");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Allocate Memory");
            System.out.println("2. Deallocate Memory");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter memory size to allocate (in KB): ");
                    int size = scanner.nextInt();
                    if (allocateMemory(size)) {
                        System.out.println("Allocated " + size + " KB.");
                    } else {
                        System.out.println("Allocation failed: Insufficient memory.");
                    }
                    printState("After Allocation");
                }
                case 2 -> {
                    System.out.print("Enter memory size to deallocate (in KB): ");
                    int size = scanner.nextInt();
                    if (deallocateMemory(size)) {
                        System.out.println("Deallocated " + size + " KB.");
                    } else {
                        System.out.println("Deallocation failed: Block not found.");
                    }
                    printState("After Deallocation");
                }
                case 3 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private boolean allocateMemory(int size) {
        int blockSize = getNextPowerOfTwo(size);
        MemoryBlock bestFitBlock = null;

        // Find the best-fit block: smallest free block that can fit the request
        for (MemoryBlock block : memoryBlocks) {
            if (block.isFree && block.size >= blockSize) {
                if (bestFitBlock == null || block.size < bestFitBlock.size) {
                    bestFitBlock = block;
                }
            }
        }

        if (bestFitBlock != null) {
            splitBlock(bestFitBlock, blockSize);
            bestFitBlock.isFree = false;
            return true;
        }

        return false; // Allocation failed
    }


    private boolean deallocateMemory(int size) {
        for (MemoryBlock block : memoryBlocks) {
            if (!block.isFree && block.size == size) {
                block.isFree = true;
                mergeBuddies();
                return true;
            }
        }
        return false; // Deallocation failed
    }

    private void splitBlock(MemoryBlock block, int size) {
        while (block.size > size) {
            int newSize = block.size / 2;
            memoryBlocks.add(new MemoryBlock(block.startAddress + newSize, newSize, true));
            block.size = newSize;

            // Stop splitting if the exact size is matched
            if (block.size == size) {
                break;
            }
        }
    }

    private void mergeBuddies() {
        memoryBlocks.sort((a, b) -> Integer.compare(a.startAddress, b.startAddress));
        for (int i = 0; i < memoryBlocks.size() - 1; i++) {
            MemoryBlock block1 = memoryBlocks.get(i);
            MemoryBlock block2 = memoryBlocks.get(i + 1);

            if (block1.isFree && block2.isFree && block1.size == block2.size
                    && block1.startAddress + block1.size == block2.startAddress) {
                block1.size *= 2;
                memoryBlocks.remove(block2);
                i--;
            }
        }
    }

    private int getNextPowerOfTwo(int size) {
        int power = 1;
        while (power < size) {
            power *= 2;
        }
        return power;
    }

    private void printState(String message) {
        System.out.println("\n" + message);
        System.out.println("Memory State:");
        for (MemoryBlock block : memoryBlocks) {
            System.out.println(block);
        }
    }

    private static class MemoryBlock {
        int startAddress;
        int size;
        boolean isFree;

        MemoryBlock(int startAddress, int size, boolean isFree) {
            this.startAddress = startAddress;
            this.size = size;
            this.isFree = isFree;
        }

        @Override
        public String toString() {
            return "Address: " + startAddress + ", Size: " + size + " KB, Free: " + isFree;
        }
    }
}
