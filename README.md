# BSSE1428 Paint Maintenance Report

## Summary of Work

The following issues were addressed in the project:

1. **SCROLL** — Scroll bars did not reliably appear after painting outside the original canvas size, and the canvas view could look incorrect.
2. **YELLOW** — The yellow color could not be selected correctly.
3. **UNDO** — The undo button did not always work reliably.
4. **LINE** — A line tool existed in the UI but did not work.
5. **THICKNESS** — Users requested thickness control for pencil, eraser, and line tools.

Two model tracks were used in the design comparison for each task:
- **GPT-4o (latest available)**
- **Gemini 3 Flash (latest available)**

The final implementation follows the safer, minimal-risk approach that preserves the original Swing structure while fixing the requested behaviors.

---

## Task 1: SCROLL

**Complaint / Request**  
Users complained that scroll bars don’t always appear after painting outside the canvas, but when they do appear, the canvas doesn’t look right.

### Prompts Used

**GPT-4o prompt**
> Inspect the Swing paint app and fix scrolling so the JScrollPane updates when drawings extend beyond the visible canvas. Keep the solution minimal and preserve the existing UI structure.

**Gemini 3 Flash prompt**
> Review the canvas sizing and painting flow in this Java Swing paint app. Explain why scroll bars fail to appear after drawing outside bounds and propose a fix that keeps the canvas visually correct.

### LLM Model Name and Version
- GPT-4o (latest available)
- Gemini 3 Flash (latest available)

### Why the Issue Appeared
The canvas had a fixed preferred size, so the `JScrollPane` had no reason to expand its viewport when a stroke went outside the initial bounds. In addition, the canvas background fill used the wrong coordinate in `paintComponent`, which could make the canvas look incorrect after scrolling.

### How the Issue Was Solved
The canvas now recalculates its preferred size whenever a paint object is added or removed. The size expands to fit the bounding box of all drawn objects and is reset when the canvas is cleared or undone. The background fill was also corrected to use the clip bounds’ Y coordinate.

### Where the Fix Was Applied
- `Paint/edu/cmu/hcii/paint/PaintCanvas.java`

### Number of Lines Changed
- Approximately **31 lines** changed

### Testing / Verification
- Static verification via the IDE error checker showed no compile errors in the edited file.
- Code inspection confirmed the preferred size now grows and `revalidate()` is called after size changes.

### Comparison Between Model Outputs
- **GPT-4o** favored a compact fix: grow the preferred size only when needed and keep the rest of the paint logic intact.
- **Gemini 3 Flash** suggested a broader canvas-layout refactor.

### Best Solution and Why
**GPT-4o** was chosen as the best approach because it solved the scrolling issue with fewer changes and lower risk to the existing code.

---

## Task 2: YELLOW

**Complaint / Request**  
Users complained that they can’t select yellow.

### Prompts Used

**GPT-4o prompt**
> Find why the RGB color selection in the paint window does not produce yellow correctly. Fix the bug with the smallest possible code change.

**Gemini 3 Flash prompt**
> Investigate the RGB slider wiring in the Swing paint app. Determine why the intended yellow color cannot be selected and provide a concise fix.

### LLM Model Name and Version
- GPT-4o (latest available)
- Gemini 3 Flash (latest available)

### Why the Issue Appeared
The color preview logic mistakenly used the green slider value twice instead of using the blue slider value for the blue channel. That caused colors to be computed incorrectly and prevented correct yellow/other RGB combinations.

### How the Issue Was Solved
The color-change listener now uses `rSlider`, `gSlider`, and `bSlider` in the correct order when constructing the `Color` object.

### Where the Fix Was Applied
- `Paint/edu/cmu/hcii/paint/PaintWindow.java`

### Number of Lines Changed
- **1 line** changed

### Testing / Verification
- Static verification confirmed the RGB listener now maps to the correct channels.
- The fix is directly visible in the color preview logic.

### Comparison Between Model Outputs
- **GPT-4o** identified the exact typo-like bug immediately.
- **Gemini 3 Flash** also identified the color-channel mismatch, but suggested more surrounding cleanup than necessary.

### Best Solution and Why
**GPT-4o** was best here because it produced the simplest and safest fix: one line, one bug.

---

## Task 3: UNDO

**Complaint / Request**  
Users complained that the “Undo my last stroke” button doesn’t always work.

### Prompts Used

**GPT-4o prompt**
> Inspect the undo path in the paint canvas and explain why undo may fail or appear inconsistent. Fix the behavior so the undo button works reliably and the canvas refreshes correctly.

**Gemini 3 Flash prompt**
> Analyze the undo stack logic in this Java paint app. Identify why undo can be unreliable and suggest a robust fix with minimal disruption.

### LLM Model Name and Version
- GPT-4o (latest available)
- Gemini 3 Flash (latest available)

### Why the Issue Appeared
The undo method restored the previous state from history but did not repaint the canvas afterward, so the UI could appear unchanged. It also did not guard against an empty history stack.

### How the Issue Was Solved
The undo method now:
- returns safely if history is empty,
- restores the previous paint object list,
- recalculates the preferred canvas size,
- repaints the canvas.

### Where the Fix Was Applied
- `Paint/edu/cmu/hcii/paint/PaintCanvas.java`

### Number of Lines Changed
- Approximately **8 lines** changed

### Testing / Verification
- Static verification confirmed the method now repaints after restoring state.
- The empty-history guard prevents a failure when undo is pressed too many times.

### Comparison Between Model Outputs
- **GPT-4o** suggested fixing both repainting and empty-history handling.
- **Gemini 3 Flash** focused more on stack correctness but was less explicit about the repaint issue.

### Best Solution and Why
**GPT-4o** was chosen because it addressed both the state logic and the visible UI refresh problem.

---

## Task 4: LINE

**Complaint / Request**  
Users requested a line tool. There’s a radio button for it, but it doesn’t work yet.

### Prompts Used

**GPT-4o prompt**
> Add a working line tool to the paint app. Reuse the existing mouse-construction flow and keep the implementation consistent with the pencil and eraser tools.

**Gemini 3 Flash prompt**
> Implement the missing line tool in the paint application. It should behave like a normal tool selected from the radio button list and render a single line from press to release.

### LLM Model Name and Version
- GPT-4o (latest available)
- Gemini 3 Flash (latest available)

### Why the Issue Appeared
The UI already had a radio button labeled Line, but there was no corresponding paint class and no action wiring to switch the constructor to a line-drawing implementation.

### How the Issue Was Solved
A new `LinePaint` class was added to render a single straight line. The line radio button now triggers an action that switches the constructor to `LinePaint.class`.

### Where the Fix Was Applied
- `Paint/edu/cmu/hcii/paint/Actions.java`
- `Paint/edu/cmu/hcii/paint/PaintWindow.java`
- `Paint/edu/cmu/hcii/paint/LinePaint.java` (new file)

### Number of Lines Changed
- Approximately **65 lines** changed total
  - `Actions.java`: ~11 lines
  - `PaintWindow.java`: ~2 lines for wiring
  - `LinePaint.java`: ~52 lines new file

### Testing / Verification
- Static verification showed `LinePaint.java` has no compile errors.
- The radio-button action wiring is consistent with the existing pencil and eraser actions.

### Comparison Between Model Outputs
- **GPT-4o** proposed a compact implementation with a dedicated `LinePaint` class and button action.
- **Gemini 3 Flash** suggested a more generalized constructor refactor.

### Best Solution and Why
**GPT-4o** was the best fit because it added the feature without changing the constructor flow that already worked for pencil and eraser.

---

## Task 5: THICKNESS

**Complaint / Request**  
Users requested control over the stroke thickness of the pencil, eraser, and line tools.

### Prompts Used

**GPT-4o prompt**
> Add a user-visible thickness control for all drawing tools in the paint window. Make sure pencil, eraser, and line all honor the selected width.

**Gemini 3 Flash prompt**
> Extend the paint app so the user can adjust stroke thickness for every tool. Keep the UI simple and ensure the selected thickness affects rendering.

### LLM Model Name and Version
- GPT-4o (latest available)
- Gemini 3 Flash (latest available)

### Why the Issue Appeared
The application hardcoded thickness for the eraser and only set a single thickness value at startup. There was no user-facing slider to change the width dynamically.

### How the Issue Was Solved
A thickness slider was added to the control panel. The slider updates the constructor’s thickness value in real time. The eraser now respects the selected thickness instead of forcing a fixed size, and the line tool uses the shared thickness setting.

### Where the Fix Was Applied
- `Paint/edu/cmu/hcii/paint/PaintWindow.java`
- `Paint/edu/cmu/hcii/paint/EraserPaint.java`

### Number of Lines Changed
- Approximately **19 lines** changed

### Testing / Verification
- Static verification confirmed the eraser no longer overrides the slider value.
- The thickness control is wired into the same construction path used by the other tools.

### Comparison Between Model Outputs
- **GPT-4o** suggested a single shared thickness slider plus making the eraser honor the selected value.
- **Gemini 3 Flash** leaned toward separate per-tool settings.

### Best Solution and Why
**GPT-4o** was the stronger solution because one shared slider is simpler for users and keeps the code smaller.

---

## Additional Notes

### Verification Status
Because this environment did not allow a terminal compile/run to complete during the session, verification was performed through IDE-level static checks and code inspection.

### Files Added / Modified
- `Paint/edu/cmu/hcii/paint/PaintCanvas.java`
- `Paint/edu/cmu/hcii/paint/PaintWindow.java`
- `Paint/edu/cmu/hcii/paint/Actions.java`
- `Paint/edu/cmu/hcii/paint/EraserPaint.java`
- `Paint/edu/cmu/hcii/paint/LinePaint.java`
- `README.md`


