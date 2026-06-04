import type { Task } from '../../types/task';
import { BadgeComponent } from './Badge.component';

/**
 * Prop definitions for the TaskCard Component.
 * Best Practice: Instead of extending the component props directly from the Task entity,
 * we encapsulate the task inside a 'task' prop. This separates our domain data structure
 * from component-specific UI triggers (like callback events).
 */
interface TaskCardProps {
  task: Task;
  onStatusChange?: (id: string) => void;
}

export function TaskCardComponent({ task, onStatusChange }: TaskCardProps) {
  const { id, title, description, priority, status } = task;

  return (
    <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-5 hover:border-zinc-700 hover:shadow-xl hover:shadow-black/25 transition-all duration-300 flex flex-col justify-between">
      <div>
        <div className="flex justify-between items-start mb-3">
          {/* Priority Badge */}
          <BadgeComponent type="priority" value={priority} />

          {/* Status Toggle Button */}
          {onStatusChange ? (
            <button
              onClick={() => onStatusChange(id)}
              className="cursor-pointer hover:opacity-80 transition-opacity focus:outline-none"
              title="Click to cycle status"
            >
              <BadgeComponent type="status" value={status} />
            </button>
          ) : (
            <BadgeComponent type="status" value={status} />
          )}
        </div>

        <h3 className="text-white font-semibold text-base mb-1 tracking-tight">
          {title}
        </h3>

        {description && (
          <p className="text-zinc-400 text-sm mt-1 line-clamp-2 leading-relaxed">
            {description}
          </p>
        )}
      </div>

      <div className="mt-4 pt-3 border-t border-zinc-800/60 flex items-center justify-between text-xs text-zinc-500 font-mono">
        <span>ID: #{id.substring(0, 8)}</span>
        {onStatusChange && (
          <button
            onClick={() => onStatusChange(id)}
            className="text-indigo-400 hover:underline cursor-pointer focus:outline-none"
          >
            Cycle Status &rarr;
          </button>
        )}
      </div>
    </div>
  );
}

