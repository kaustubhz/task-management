import { useState } from 'react';
import { TaskCardComponent } from './components/ui/TaskCard.component';
import type { Task } from './types/task';

const INITIAL_TASKS: Task[] = [
  { id: '1', title: 'Reorganize project workspace', status: 'COMPLETED', priority: 'HIGH' },
  { id: '2', title: 'Configure Tailwind CSS v4 and Vite', status: 'COMPLETED', priority: 'HIGH' },
  { id: '3', title: 'Build React component library skeleton', status: 'IN_PROGRESS', priority: 'MEDIUM' },
  { id: '4', title: 'Hook up Spring Boot API services', status: 'TODO', priority: 'LOW' },
];

export default function App() {
  const [tasks, setTasks] = useState<Task[]>(INITIAL_TASKS);
  const [filter, setFilter] = useState<'all' | 'TODO' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'>('all');

  // Filter tasks based on selected state
  const filteredTasks = tasks.filter(task => {
    if (filter === 'all') return true;
    return task.status === filter;
  });

  // Helper function to toggle a task status (Demo of state management mutation best practices)
  const toggleTaskStatus = (taskId: string) => {
    setTasks(prevTasks =>
      prevTasks.map(task => {
        if (task.id === taskId) {
          const statusOrder: Task['status'][] = ['TODO', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
          const currentIndex = statusOrder.indexOf(task.status);
          const nextStatus = statusOrder[(currentIndex + 1) % statusOrder.length];
          return { ...task, status: nextStatus };
        }
        return task;
      })
    );
  };

  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-100 flex flex-col font-sans select-none">
      {/* Top Navigation Header */}
      <header className="border-b border-zinc-800 bg-zinc-900/50 backdrop-blur-md sticky top-0 z-50">
        <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center font-bold text-white shadow-lg shadow-indigo-500/20">
              T
            </div>
            <span className="font-semibold text-lg tracking-tight">TaskFlow <span className="text-xs text-indigo-400 font-mono">v1.0</span></span>
          </div>
          <div className="flex items-center gap-4">
            <div className="text-xs text-zinc-400 px-3 py-1 bg-zinc-800 rounded-full font-mono border border-zinc-700">
              Sprint 1
            </div>
          </div>
        </div>
      </header>

      {/* Main Workspace Area */}
      <main className="flex-1 max-w-6xl w-full mx-auto px-6 py-10">
        {/* Banner Section */}
        <section className="mb-10">
          <h1 className="text-4xl font-extrabold tracking-tight text-white mb-2">
            Welcome to your Task Management Dashboard
          </h1>
          <p className="text-zinc-400 text-lg max-w-2xl">
            This is a React + TypeScript + Tailwind CSS v4 skeleton project. 
            Click on any task badge to cycle its status. Let's start building!
          </p>
        </section>

        {/* Action Panel */}
        <section className="flex flex-wrap items-center justify-between gap-4 mb-8 bg-zinc-900 border border-zinc-800 p-4 rounded-xl">
          <div className="flex items-center gap-2">
            <span className="text-sm font-medium text-zinc-400 mr-2">Filter status:</span>
            {(['all', 'TODO', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'] as const).map(option => (
              <button
                key={option}
                onClick={() => setFilter(option)}
                className={`px-3 py-1.5 rounded-lg text-xs font-semibold uppercase tracking-wider transition-all duration-200 cursor-pointer ${
                  filter === option
                    ? 'bg-indigo-600 text-white shadow-md shadow-indigo-500/10'
                    : 'bg-zinc-800 text-zinc-300 hover:bg-zinc-750'
                }`}
              >
                {option.toLowerCase().replace('_', ' ')}
              </button>
            ))}
          </div>

          <div className="text-sm text-zinc-400 font-mono">
            Showing {filteredTasks.length} / {tasks.length} tasks
          </div>
        </section>

        {/* Task Grid / List View */}
        <section className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredTasks.map(task => (
            <TaskCardComponent
              key={task.id}
              task={task}
              onStatusChange={toggleTaskStatus}
            />
          ))}

          {filteredTasks.length === 0 && (
            <div className="col-span-full py-12 text-center border border-dashed border-zinc-800 rounded-xl text-zinc-500">
              No tasks found matching this filter.
            </div>
          )}
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t border-zinc-900 bg-zinc-950 py-8 text-center text-zinc-600 text-xs font-mono">
        <p>&copy; 2026 TaskFlow. Pair-programming with Antigravity.</p>
      </footer>
    </div>
  );
}
