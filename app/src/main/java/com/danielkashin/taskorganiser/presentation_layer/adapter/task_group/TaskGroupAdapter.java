package com.danielkashin.taskorganiser.presentation_layer.adapter.task_group;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.danielkashin.taskorganiser.R;
import com.danielkashin.taskorganiser.domain_layer.helper.ExceptionHelper;
import com.danielkashin.taskorganiser.domain_layer.pojo.DateTypeTaskGroup;
import com.danielkashin.taskorganiser.domain_layer.pojo.ITaskGroup;
import com.danielkashin.taskorganiser.domain_layer.pojo.Task;
import com.danielkashin.taskorganiser.presentation_layer.adapter.tags.ITagsAdapter;
import com.danielkashin.taskorganiser.presentation_layer.adapter.tags.TagsAdapter;

import java.util.ArrayList;
import java.util.UUID;

public class TaskGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ITaskGroupAdapter, ITagsAdapter.Callbacks {

  private final static int VIEW_TYPE_TASK = 21245;
  private final static int VIEW_TYPE_EDIT_TEXT = 12245;

  private ITaskGroupAdapter.Callbacks mCallbacks;
  private ITaskGroup mTaskGroup;


  public TaskGroupAdapter() {
  }

  public TaskGroupAdapter(ITaskGroup taskGroup) {
    ExceptionHelper.checkAllObjectsNonNull("All adapter arguments must be non null", taskGroup);

    mTaskGroup = taskGroup;
  }

  // -------------------------------- ITagsAdapter.Callbacks --------------------------------------

  @Override
  public void onTagClicked(String tagName) {
    if (mCallbacks != null) {
      mCallbacks.onTagClicked(tagName);
    }
  }

  // ----------------------------------- ITaskGroupAdapter  ---------------------------------------

  @Override
  public void attachCallbacks(Callbacks callbacks) {
    mCallbacks = callbacks;
  }

  @Override
  public void detachCallbacks() {
    mCallbacks = null;
  }

  @Override
  public void addTask(Task task) {
    ExceptionHelper.assertFalse("DateTypeTaskGroup is null", mTaskGroup == null);

    int insertedIndex = mTaskGroup.addTask(task);
    notifyItemInserted(insertedIndex);
  }

  @Override
  public void changeTaskGroup(ITaskGroup taskGroup) {
    mTaskGroup = taskGroup;
    mTaskGroup.sort();
    notifyDataSetChanged();
  }

  // ------------------------------------- RecyclerView.Adapter -----------------------------------

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_TASK) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_task, parent, false);
      return new TaskViewHolder(view, parent.getContext());
    } else if (viewType == VIEW_TYPE_EDIT_TEXT) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_edit_text, parent, false);
      return new EditTextViewHolder(view);
    } else {
      throw new IllegalStateException("Unknown adapter view type");
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    boolean holderIsTask = getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_TASK
        && holder instanceof TaskViewHolder;
    boolean holderIsEditText = getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_EDIT_TEXT
        && holder instanceof EditTextViewHolder;

    if (holderIsTask) {
      final Task task = mTaskGroup.getTask(holder.getAdapterPosition());
      TaskViewHolder taskViewHolder = (TaskViewHolder) holder;

      // set name
      taskViewHolder.setTextName(task.getName());

      // set time
      if (mTaskGroup instanceof DateTypeTaskGroup) {
        if (((DateTypeTaskGroup) mTaskGroup).getType() == Task.Type.Day) {
          taskViewHolder.setTextTime(task.getTimeToString());
        } else {
          taskViewHolder.setTextTime("");
        }
      }

      // set tags
      taskViewHolder.setTags(task.getTags(), this);

      // set important toggle
      taskViewHolder.setToggleImportantChecked(task.getImportant());
      taskViewHolder.setOnToggleImportantClickedListener(new TaskViewHolder.OnToggleClickedListener() {
        @Override
        public void onToggleClicked(boolean isChecked) {
          if (mCallbacks != null) {
            task.setImportant(isChecked);
            mCallbacks.onTaskChanged(task);
          }
        }
      });

      // set done toggle
      taskViewHolder.setToggleDoneChecked(task.getDone());
      taskViewHolder.setOnToggleDoneClickedListener(new TaskViewHolder.OnToggleClickedListener() {
        @Override
        public void onToggleClicked(boolean isChecked) {
          if (mCallbacks != null) {
            task.setDone(isChecked);
            mCallbacks.onTaskChanged(task);
          }
        }
      });
    } else if (holderIsEditText) {
      if (mTaskGroup instanceof DateTypeTaskGroup) {
        ((EditTextViewHolder) holder).setOnTextChangedListener(new EditTextViewHolder.OnTextChangedListener() {
          @Override
          public void onTextChanged(String text) {
            if (mCallbacks != null) {
              Task task = new Task(text,
                  UUID.randomUUID().toString(),
                  ((DateTypeTaskGroup) mTaskGroup).getType(),
                  ((DateTypeTaskGroup) mTaskGroup).getDate());

              mCallbacks.onTaskChanged(task);
            }
          }
        });
      }
    }
  }

  @Override
  public int getItemCount() {
    if (mTaskGroup == null) {
      return 0;
    } else {
      return mTaskGroup.getTaskSize() + 1;
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (position == mTaskGroup.getTaskSize()) {
      return VIEW_TYPE_EDIT_TEXT;
    } else {
      return VIEW_TYPE_TASK;
    }
  }

  // --------------------------------------- inner types ------------------------------------------

  private static class TaskViewHolder extends RecyclerView.ViewHolder {

    private TextView textName;
    private TextView textTime;
    private ToggleButton toggleDone;
    private ToggleButton toggleImportant;
    private RecyclerView recyclerTags;
    private OnToggleClickedListener onToggleDoneClickedListener;
    private OnToggleClickedListener onToggleImportantClickedListener;

    private TaskViewHolder(View view, Context context) {
      super(view);
      textName = (TextView) view.findViewById(R.id.text_name);
      textTime = (TextView) view.findViewById(R.id.text_time);
      toggleDone = (ToggleButton) view.findViewById(R.id.toggle_done);
      toggleImportant = (ToggleButton) view.findViewById(R.id.toggle_important);
      recyclerTags = (RecyclerView) view.findViewById(R.id.recycler_tags);
      recyclerTags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
      recyclerTags.addItemDecoration(new SpacingItemDecoration(18, 0));
      recyclerTags.setAdapter(new TagsAdapter());
      setOnToggleDoneClickedListenerLocal();
    }

    private void setTextName(String name) {
      textName.setText(name);
    }

    private void setTextTime(String time) {
      textTime.setText(time);
    }

    private void setTags(ArrayList<String> tags, ITagsAdapter.Callbacks callbacks) {
      if (recyclerTags.getAdapter() != null) {
        ((ITagsAdapter) recyclerTags.getAdapter()).setTags(tags);
        ((ITagsAdapter) recyclerTags.getAdapter()).attachCallbacks(callbacks);
      }
    }

    // ----------------------------------- toggle important ---------------------------------------

    private void setToggleImportantChecked(boolean checked) {
      toggleImportant.setOnCheckedChangeListener(null);
      toggleImportant.setChecked(checked);
      setOnToggleImportantClickedListenerLocal();
    }

    private void setOnToggleImportantClickedListener(OnToggleClickedListener onToggleClickedListener) {
      this.onToggleImportantClickedListener = onToggleClickedListener;
      setOnToggleImportantClickedListenerLocal();
    }

    private void setOnToggleImportantClickedListenerLocal() {
      toggleImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if (onToggleImportantClickedListener != null) {
            onToggleImportantClickedListener.onToggleClicked(isChecked);
          }
        }
      });
    }

    // ------------------------------------- toggle done ------------------------------------------

    private void setToggleDoneChecked(boolean checked) {
      toggleDone.setOnCheckedChangeListener(null);
      toggleDone.setChecked(checked);

      if (checked) {
        textName.setPaintFlags(textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      } else {
        textName.setPaintFlags(textName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
      }

      setOnToggleDoneClickedListenerLocal();
    }

    private void setOnToggleDoneClickedListener(OnToggleClickedListener onToggleClickedListener) {
      this.onToggleDoneClickedListener = onToggleClickedListener;
      setOnToggleDoneClickedListenerLocal();
    }

    private void setOnToggleDoneClickedListenerLocal() {
      toggleDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if (onToggleDoneClickedListener != null) {
            onToggleDoneClickedListener.onToggleClicked(isChecked);
          }
        }
      });
    }

    // --------------------------------------- inner types ----------------------------------------

    private interface OnToggleClickedListener {

      void onToggleClicked(boolean isChecked);

    }
  }

  private static class EditTextViewHolder extends RecyclerView.ViewHolder {

    private EditText editText;
    private OnTextChangedListener onTextChangedListener;


    private EditTextViewHolder(View view) {
      super(view);
      editText = (EditText) view.findViewById(R.id.edit_text);
      setOnTextChangedListenerLocal();
    }

    private void setOnTextChangedListener(OnTextChangedListener listener) {
      this.onTextChangedListener = listener;
    }

    private void setOnTextChangedListenerLocal() {
      editText.addTextChangedListener(new TextWatcher() {
        // fields to perform delay
        private final static int INPUT_DELAY_IN_MS = 1000;
        private Handler handler = new Handler(Looper.getMainLooper());
        Runnable workRunnable;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
          handler.removeCallbacks(workRunnable);
        }

        @Override
        public void afterTextChanged(final Editable editable) {
          if (!editable.toString().trim().isEmpty()) {
            workRunnable = new Runnable() {
              @Override
              public void run() {
                if (onTextChangedListener != null) {
                  String text = editable.toString().trim();
                  onTextChangedListener.onTextChanged(text);
                  editText.setText("");
                }
              }
            };
            handler.postDelayed(workRunnable, INPUT_DELAY_IN_MS);
          }
        }
      });
    }

    private interface OnTextChangedListener {

      void onTextChanged(String text);

    }
  }
}