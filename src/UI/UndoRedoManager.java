package UI;

import Domain.Patient;
import Domain.Appointment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * UndoRedoManager provides undo/redo functionality for tracking and reversing operations
 * in the appointment management system.
 * 
 * This manager maintains two stacks: one for undo operations and one for redo operations.
 * Each operation stores the action type, affected entity, and previous state for restoration.
 */
public class UndoRedoManager {
    private Stack<UndoRedoAction> undoStack;
    private Stack<UndoRedoAction> redoStack;
    private int maxHistorySize = 100;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public UndoRedoManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    /**
     * Adds a new action to the undo stack with associated state for restoration.
     * When a new action is added, the redo stack is cleared.
     * 
     * @param actionType Type of action (ADD, UPDATE, DELETE)
     * @param entityType Type of entity (PATIENT, APPOINTMENT)
     * @param description Description of the action
     * @param previousState The previous state (before the action)
     * @param currentState The current state (after the action)
     */
    public void addAction(String actionType, String entityType, String description, 
                         Object previousState, Object currentState) {
        UndoRedoAction action = new UndoRedoAction(
            actionType, entityType, description, 
            LocalDateTime.now(), previousState, currentState
        );
        undoStack.push(action);
        
        // Clear redo stack when new action is performed
        redoStack.clear();
        
        // Prevent unlimited memory usage
        if (undoStack.size() > maxHistorySize) {
            undoStack.removeElementAt(0);
        }
    }
    
    /**
     * Performs an undo operation by moving the last action from undo stack to redo stack.
     * Returns the action that was undone so the caller can restore the previous state.
     * 
     * @return The UndoRedoAction that was undone, or null if nothing to undo
     */
    public UndoRedoAction undo() {
        if (undoStack.isEmpty()) {
            return null;
        }
        
        UndoRedoAction action = undoStack.pop();
        redoStack.push(action);
        return action;
    }
    
    /**
     * Performs a redo operation by moving an action from redo stack back to undo stack.
     * Returns the action that was redone so the caller can restore the current state.
     * 
     * @return The UndoRedoAction that was redone, or null if nothing to redo
     */
    public UndoRedoAction redo() {
        if (redoStack.isEmpty()) {
            return null;
        }
        
        UndoRedoAction action = redoStack.pop();
        undoStack.push(action);
        return action;
    }
    
    /**
     * Gets the entire history of actions (both undo and redo).
     * Format: "[HH:mm:ss] Action Description [UNDONE]" or "[HH:mm:ss] Action Description"
     * 
     * @return List of formatted history strings
     */
    public List<String> getHistory() {
        List<String> history = new ArrayList<>();
        
        // Add undo stack items (most recent first)
        for (int i = undoStack.size() - 1; i >= 0; i--) {
            UndoRedoAction action = undoStack.get(i);
            history.add(String.format("[%s] %s",
                action.getTimestamp().format(formatter),
                action.getDescription()
            ));
        }
        
        // Add redo stack items (marked as undone)
        for (int i = redoStack.size() - 1; i >= 0; i--) {
            UndoRedoAction action = redoStack.get(i);
            history.add(String.format("[%s] %s [UNDONE]",
                action.getTimestamp().format(formatter),
                action.getDescription()
            ));
        }
        
        return history;
    }
    
    /**
     * Gets the current size of the undo stack.
     * 
     * @return Number of actions that can be undone
     */
    public int getUndoCount() {
        return undoStack.size();
    }
    
    /**
     * Gets the current size of the redo stack.
     * 
     * @return Number of actions that can be redone
     */
    public int getRedoCount() {
        return redoStack.size();
    }
    
    /**
     * Checks if there are any actions available to undo.
     * 
     * @return true if undo is possible, false otherwise
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    /**
     * Checks if there are any actions available to redo.
     * 
     * @return true if redo is possible, false otherwise
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
    
    /**
     * Clears both undo and redo stacks, resetting the history.
     */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
    
    /**
     * Gets a summary of the last action that can be undone.
     * 
     * @return Description of the last action, or null if no actions to undo
     */
    public String getLastUndoAction() {
        if (undoStack.isEmpty()) {
            return null;
        }
        return undoStack.peek().getDescription();
    }
    
    /**
     * Gets a summary of the last action that can be redone.
     * 
     * @return Description of the last action, or null if no actions to redo
     */
    public String getLastRedoAction() {
        if (redoStack.isEmpty()) {
            return null;
        }
        return redoStack.peek().getDescription();
    }
    
    /**
     * Sets the maximum number of actions to keep in history.
     * This prevents unlimited memory usage.
     * 
     * @param maxSize Maximum number of actions to store
     */
    public void setMaxHistorySize(int maxSize) {
        this.maxHistorySize = maxSize;
    }
    
    /**
     * Inner class representing a single action in the undo/redo history.
     * Stores both the previous state (for undo) and current state (for redo).
     */
    public static class UndoRedoAction {
        private final String actionType;        // ADD, UPDATE, DELETE
        private final String entityType;        // PATIENT, APPOINTMENT
        private final String description;
        private final LocalDateTime timestamp;
        private final Object previousState;     // State before action
        private final Object currentState;      // State after action
        
        public UndoRedoAction(String actionType, String entityType, String description, 
                             LocalDateTime timestamp, Object previousState, Object currentState) {
            this.actionType = actionType;
            this.entityType = entityType;
            this.description = description;
            this.timestamp = timestamp;
            this.previousState = previousState;
            this.currentState = currentState;
        }
        
        public String getActionType() {
            return actionType;
        }
        
        public String getEntityType() {
            return entityType;
        }
        
        public String getDescription() {
            return description;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public Object getPreviousState() {
            return previousState;
        }
        
        public Object getCurrentState() {
            return currentState;
        }
    }
}
