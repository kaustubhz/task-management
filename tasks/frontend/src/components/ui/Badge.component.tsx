import type { TaskStatus, TaskPriority } from '../../types/task';

/**
 * Prop definitions for the Badge component.
 * We use a discriminated union to ensure that if type is 'status',
 * the value MUST be a TaskStatus, and if type is 'priority', it MUST be a TaskPriority.
 */
type BadgeProps =
  | {
      type: 'status';
      value: TaskStatus;
    }
  | {
      type: 'priority';
      value: TaskPriority;
    };

/**
 * BadgeComponent is a pure presentational component.
 * Best Practice: Avoid using hooks like useEffect inside simple UI primitives unless
 * you are performing side-effects (like connecting to a third-party library or event listener).
 * Keeping them "pure" makes them fast and easy to test.
 */
export function BadgeComponent(props: BadgeProps) {
  const { value } = props;
  // Define Tailwind CSS v4 styling mapping for each status / priority variant
  const getStyles = () => {
    switch (value) {
      // Statuses
      case 'TODO':
        return 'bg-zinc-800/60 text-zinc-400 border border-zinc-700/50';
      case 'IN_PROGRESS':
        return 'bg-blue-500/10 text-blue-400 border border-blue-500/20';
      case 'COMPLETED':
        return 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20';
      case 'CANCELLED':
        return 'bg-zinc-900 text-zinc-600 border border-zinc-800';

      // Priorities
      case 'LOW':
        return 'bg-zinc-800/40 text-zinc-400 border border-zinc-700/40';
      case 'MEDIUM':
        return 'bg-amber-500/10 text-amber-400 border border-amber-500/25';
      case 'HIGH':
        return 'bg-rose-500/10 text-rose-400 border border-rose-500/25';

      default:
        return 'bg-zinc-800 text-zinc-300 border border-zinc-750';
    }
  };

  // Convert status codes like "IN_PROGRESS" to human-readable "In Progress"
  const getLabel = () => {
    return value.toLowerCase().replace('_', ' ');
  };

  return (
    <span
      className={`inline-flex items-center px-2 py-0.5 rounded text-[10px] font-semibold uppercase tracking-wider transition-colors duration-200 ${getStyles()}`}
    >
      {getLabel()}
    </span>
  );
}
