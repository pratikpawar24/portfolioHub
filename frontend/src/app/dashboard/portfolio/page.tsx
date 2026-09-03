import { EmptyState } from "@/components/states/EmptyState";

// Portfolio CMS lands in Phase 2, against the B2 portfolio domain/schema
// backend milestone.
export default function PortfolioOverviewPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Portfolio</h1>
      <EmptyState
        heading="Nothing written yet"
        description="Once the portfolio API is connected, you'll edit your profile, projects, skills and more here."
      />
    </div>
  );
}
