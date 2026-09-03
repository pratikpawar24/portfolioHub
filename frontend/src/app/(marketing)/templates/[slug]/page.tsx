import { EmptyState } from "@/components/states/EmptyState";

export default async function TemplateDetailPage({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;

  return (
    <div className="flex flex-col gap-8">
      <EmptyState
        heading="This template isn't available yet"
        description={`The template catalogue isn't connected yet, so "${slug}" can't be shown. Once it is, this page will show the full preview, creator, framework, license and schema compatibility.`}
      />
    </div>
  );
}
