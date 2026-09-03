import { EmptyState } from "@/components/states/EmptyState";

// BYO hosting (Vercel/Netlify/Cloudflare) is V3 / Phase 8. This page
// must only ever launch the backend-supported OAuth-style authorization
// flow and show connection state — never collect provider credentials
// directly, per FrontendDevelopmentPrompt.md §9.
export default function HostingConnectionsPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl">Hosting connections</h1>
      <EmptyState
        heading="Hosting connections aren't available yet"
        description="Connecting Vercel, Netlify or Cloudflare is a later release. PortfolioHub's own hosting is the default for publishing."
      />
    </div>
  );
}
