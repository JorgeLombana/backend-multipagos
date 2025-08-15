import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { GRADIENTS } from '@/lib/theme';
import { cn } from '@/lib/utils';
import type { BaseCardProps } from '@/types/enhanced-types';

export function GradientCard({
  title,
  description,
  gradient = GRADIENTS.primary,
  icon: Icon,
  className,
  children,
  ...props
}: BaseCardProps) {
  return (
    <Card className={cn('shadow-lg overflow-hidden !p-0 border border-slate-200', className)} {...props}>
      <CardHeader className={cn(gradient, 'text-white !m-0 p-4')}>
        <CardTitle className="flex items-center text-white text-sm font-semibold">
          {Icon && <Icon className="h-4 w-4 mr-2" />}
          {title}
        </CardTitle>
        {description && <p className="text-xs mt-1 opacity-90">{description}</p>}
      </CardHeader>
      <CardContent className="p-4">{children}</CardContent>
    </Card>
  );
}

interface StatsCardProps {
  title: string;
  value: string | number;
  description: string;
  icon: React.ComponentType<{ className?: string }>;
  gradient: string;
  valueColor?: string;
  isPlaceholder?: boolean;
}

export function StatsCard({
  title,
  value,
  description,
  icon: Icon,
  gradient,
  valueColor = '',
  isPlaceholder = false,
}: StatsCardProps) {
  return (
    <Card className={cn('shadow-md overflow-hidden border border-slate-200', isPlaceholder && 'opacity-60')}>
      <div className={cn('h-2', gradient)}></div>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium text-slate-700">{title}</CardTitle>
        <div className={cn('p-2 rounded-full', gradient)}>
          <Icon className="h-4 w-4 text-white" />
        </div>
      </CardHeader>
      <CardContent>
        <div className={cn('text-2xl font-bold', valueColor || 'text-slate-800')}>{value}</div>
        <p className="text-xs text-slate-500">{description}</p>
      </CardContent>
    </Card>
  );
}

interface LoadingCardProps {
  title: string;
  description?: string;
  height?: string;
}

export function LoadingCard({ title, description, height = 'h-32' }: LoadingCardProps) {
  return (
    <Card className="border-0 shadow-md">
      <CardHeader>
        <CardTitle>{title}</CardTitle>
        {description && <p className="text-muted-foreground text-sm">{description}</p>}
      </CardHeader>
      <CardContent>
        <div className={cn('flex items-center justify-center', height)}>
          <div className="animate-pulse space-y-4 w-full">
            <div className="h-4 bg-gray-200 rounded w-3/4"></div>
            <div className="h-4 bg-gray-200 rounded w-1/2"></div>
            <div className="h-4 bg-gray-200 rounded w-5/6"></div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
