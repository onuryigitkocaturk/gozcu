export function Spinner() {
  return <span className="spinner" />;
}

export function SpinnerCenter() {
  return (
    <div className="spinner-center">
      <Spinner />
    </div>
  );
}
