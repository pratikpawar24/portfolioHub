export default async function PublicPortfolioPage({
  params,
}: {
  params: Promise<{ username: string }>;
}) {
  const { username } = await params;

  return (
    <div className="mx-auto max-w-lg px-6 py-24 text-center">
      <h1 className="text-xl">This portfolio isn&apos;t live yet</h1>
      <p className="mt-3 text-[var(--color-ink-muted)]">
        Publishing isn&apos;t connected yet, so <span className="font-mono">/p/{username}</span>{" "}
        can&apos;t be shown.
      </p>
    </div>
  );
}
