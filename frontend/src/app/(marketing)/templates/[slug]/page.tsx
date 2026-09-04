import { TemplateDetailView } from "@/components/templates/TemplateDetailView";

export default async function TemplateDetailPage({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const { slug } = await params;
  return <TemplateDetailView slug={slug} />;
}
