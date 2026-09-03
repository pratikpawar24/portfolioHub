import { EmptyState } from "@/components/states/EmptyState";

export default function PortfolioEditorPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Editor</h1>
      <EmptyState
        heading="The editor isn't connected yet"
        description="This will hold the profile, about, skills, projects, experience, education and links sections once the portfolio API exists."
      />
    </div>
  );
}
